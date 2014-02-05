package schola
package oadmin

package impl

trait CachingAccessControlServicesComponentImpl extends CachingServicesComponent with AccessControlServicesComponent {
  self: CacheSystemProvider =>

  trait CachingAccessControlServicesImpl extends AccessControlServices {

    case object RoleParams extends CacheActor.Params {
      val cacheKey = "roles"
    }

    abstract override def getRoles =
      cachingServices.get[List[RoleLike]](RoleParams) { super.getRoles } getOrElse Nil

    abstract override def getRole(roleName: String) =
      cachingServices.get[Option[RoleLike]](Params(roleName)) { super.getRole(roleName) } flatten

    abstract override def saveRole(name: String, parent: Option[String], createdBy: Option[String]) =
      super.saveRole(name, parent, createdBy) collect {
        case my =>
          cachingServices.evict(Params(my.name))
          cachingServices.evict(RoleParams)
          my
      }

    abstract override def updateRole(name: String, newName: String, parent: Option[String]) =
      super.updateRole(name, newName, parent) && {
        cachingServices.evict(Params(newName))
        cachingServices.evict(RoleParams)
        true
      }

    abstract override def purgeRoles(roles: Set[String]) {
      super.purgeRoles(roles)
      roles foreach (o => cachingServices.evict(Params(o)))
      cachingServices.evict(RoleParams)
    }
  }
}

trait CachingOAuthServicesComponentImpl extends CachingServicesComponent with OAuthServicesComponent {
  self: CacheSystemProvider =>

  trait CachingOAuthServicesImpl extends OAuthServices {

    class UserParams private (calcPage: () => Int) extends CacheActor.Params {
      lazy val cacheKey = s"users_${calcPage()}"
    }

    object UserParams {
      def apply(page: Int = 0): UserParams = apply(() => page)
      def apply(calcPage: () => Int) = new UserParams(calcPage)
    }

    private def pageOf(userId: String) =
      () => oauthService.getPage(userId)

    abstract override def getUsers(page: Int) =
      cachingServices.get[List[UserLike]](UserParams(page)) { super.getUsers(page) } getOrElse Nil

    abstract override def getUser(id: String) =
      cachingServices.get[Option[UserLike]](Params(id)) { super.getUser(id) } flatten

    abstract override def saveUser(username: String, password: String, givenName: String, familyName: String, createdBy: Option[String], gender: domain.Gender.Value, homeAddress: Option[domain.AddressInfo], workAddress: Option[domain.AddressInfo], contacts: domain.Contacts, changePasswordAtNextLogin: Boolean)(implicit system: akka.actor.ActorSystem) =
      super.saveUser(username, password, givenName, familyName, createdBy, gender, homeAddress, workAddress, contacts, changePasswordAtNextLogin) collect {
        case my =>
          cachingServices.evict(Params(my.id.get.toString))

          cachingServices.evict(UserParams(calcPage = pageOf(my.id.get.toString)))

          my
      }

    abstract override def updateUser(id: String, spec: domain.UserSpec)(implicit system: akka.actor.ActorSystem) =
      super.updateUser(id, spec) && {

        cachingServices.evict(Params(id))

        cachingServices.evict(UserParams(calcPage = pageOf(id)))

        true
      }

    abstract override def removeUser(id: String): Boolean =
      super.removeUser(id) && {
        cachingServices.evict(Params(id))

        cachingServices.evict(UserParams(calcPage = pageOf(id)))

        true
      }

    abstract override def purgeUsers(users: Set[String]) {
      super.purgeUsers(users)
      users foreach (o => { cachingServices.evict(Params(o)); cachingServices.evict(UserParams(calcPage = pageOf(o))) })
    }
  }
}

trait CachingServicesComponentImpl extends CachingServicesComponent {
  self: CacheSystemProvider =>

  import scala.concurrent.duration._
  import akka.pattern._
  import akka.util.Timeout
  import scala.util.control.Exception.allCatch
  import scala.concurrent.Await

  protected val cachingServices = new CachingServicesImpl

  protected lazy val cacheActor = cacheSystem.createCacheActor("OAdmin", new OAdminCacheActor(_))

  class CachingServicesImpl extends CachingServices {

    implicit def asCacheParams(params: Params) = params.asInstanceOf[CacheActor.Params]

    def get[T: scala.reflect.ClassTag](params: Params)(default: => T) = {
      implicit val tm = Timeout(60 seconds)

      val q = (cacheActor ? CacheActor.FindValue(params, () => default)).mapTo[Option[T]]

      allCatch[Option[T]].opt {
        Await.result(q, tm.duration)
      } getOrElse None
    }

    def evict(params: Params) {
      cacheActor ! CacheActor.PurgeValue(params)
    }
  }
}