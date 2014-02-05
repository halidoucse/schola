package schola
package oadmin

package impl

import schola.oadmin.impl.CacheActor.{ FindValue, PurgeValue }

trait OAuthServicesComponentImpl extends OAuthServicesComponent {
  self: OAuthServicesRepoComponent =>

  class OAuthServicesImpl extends OAuthServices {

    def getUsersStats = oauthServiceRepo.getUsersStats

    def getUsers(page: Int) = oauthServiceRepo.getUsers(page)

    def getUser(id: String) = oauthServiceRepo.getUser(id)

    def removeUser(id: String) = oauthServiceRepo.removeUser(id)

    def getPurgedUsers = oauthServiceRepo.getPurgedUsers

    def purgeUsers(users: Set[String]) = oauthServiceRepo.purgeUsers(users)

    def undeleteUsers(users: Set[String]) = oauthServiceRepo.undeleteUsers(users)

    def getTokenSecret(accessToken: String) = oauthServiceRepo.getTokenSecret(accessToken)

    def getRefreshToken(refreshToken: String) = oauthServiceRepo.getRefreshToken(refreshToken)

    def exchangeRefreshToken(refreshToken: String) = oauthServiceRepo.exchangeRefreshToken(refreshToken)

    def getUserTokens(userId: String) = oauthServiceRepo.getUserTokens(userId)

    def getUserSession(params: Map[String, String]) = oauthServiceRepo.getUserSession(params)

    def revokeToken(accessToken: String) = oauthServiceRepo.revokeToken(accessToken)

    def getClient(id: String, secret: String) = oauthServiceRepo.getClient(id, secret)

    def authUser(username: String, password: String) = oauthServiceRepo.authUser(username, password)

    def saveToken(accessToken: String, refreshToken: Option[String], macKey: String, uA: String, clientId: String, redirectUri: String, userId: String, expiresIn: Option[Long], refreshExpiresIn: Option[Long], scopes: Set[String]) =
      oauthServiceRepo.saveToken(accessToken, refreshToken, macKey, uA, clientId, redirectUri, userId, expiresIn, refreshExpiresIn, scopes)

    def saveUser(username: String, password: String, givenName: String, familyName: String, createdBy: Option[String], gender: domain.Gender, homeAddress: Option[domain.AddressInfo], workAddress: Option[domain.AddressInfo], contacts: domain.Contacts, changePasswordAtNextLogin: Boolean)(implicit system: akka.actor.ActorSystem) = oauthServiceRepo.saveUser(username, password, givenName, familyName, createdBy, gender, homeAddress, workAddress, contacts, changePasswordAtNextLogin)

    def updateUser(id: String, spec: domain.UserSpec)(implicit system: akka.actor.ActorSystem) = oauthServiceRepo.updateUser(id, spec)

    def getAvatar(id: String) = {
      import scala.concurrent.duration._
      import akka.pattern._
      import scala.util.control.Exception.allCatch
      import scala.concurrent.Await

      implicit val timeout = akka.util.Timeout(5 seconds) // needed for `?` below

      (avatarService ? Avatars.Get(id)).mapTo[(String, Option[String], Array[Byte])]
    }

    def uploadAvatar(userId: String, filename: String, contentType: Option[String], bytes: Array[Byte])(implicit system: akka.actor.ActorSystem) = {
      import scala.concurrent.duration._
      import akka.pattern._

      import system.dispatcher

      implicit val timeout = akka.util.Timeout(5 seconds) // needed for `?` below

      val q = (avatarService ? Avatars.Save(userId, filename, contentType, bytes)).mapTo[String]

      q map {
        avatarId =>

          updateUser(userId, new domain.DefaultUserSpec {
            override lazy val avatar = UpdateSpecImpl[String](set = Some(Some(avatarId)))
          })

      } recover {
        case _: Throwable => false
      }
    }

    def purgeAvatar(userId: String, avatarId: String)(implicit system: akka.actor.ActorSystem) = {
      import scala.concurrent.duration._
      import akka.pattern._

      import system.dispatcher

      implicit val timeout = akka.util.Timeout(5 seconds) // needed for `?` below

      val ok = (avatarService ? Avatars.Purge(avatarId)).mapTo[Boolean]

      ok map {
        k =>

          k && updateUser(userId, new domain.DefaultUserSpec {
            override lazy val avatar = UpdateSpecImpl[String](set = Some(None))
          })

      } recover {
        case _: Throwable => false
      }
    }

    def purgeAvatarForUser(userId: String) {
      avatarService ! Avatars.PurgeForUser(userId)
    }

    def primaryEmailExists(primaryEmail: String) = oauthServiceRepo.primaryEmailExists(primaryEmail)

    def createPasswdResetReq(username: String)(implicit system: akka.actor.ActorSystem) = oauthServiceRepo.createPasswdResetReq(username)

    def checkActivationReq(username: String, ky: String) = oauthServiceRepo.checkActivationReq(username, ky)

    def resetPasswd(username: String, ky: String, newPasswd: String)(implicit system: akka.actor.ActorSystem) = oauthServiceRepo.resetPasswd(username, ky, newPasswd)

    def getPage(userId: String) = oauthServiceRepo.getPage(userId)
  }
}

trait OAuthServicesRepoComponentImpl extends OAuthServicesRepoComponent {
  self: OAuthServicesComponent with AccessControlServicesComponent with LabelServicesComponent =>

  import schema._
  import domain._
  import Q._

  private[this] val log = Logger("oadmin.oauthserviceRepoImpl")

  protected val db: Database

  protected val oauthServiceRepo = new OAuthServicesRepoImpl

  class OAuthServicesRepoImpl extends OAuthServicesRepo {

    private[this] object oq {

      object Convs {
        import scala.slick.jdbc._

        implicit object SetUUIDOption extends SetParameter[Option[java.util.UUID]] { def apply(v: Option[java.util.UUID], pp: PositionedParameters) { pp.setObjectOption(v, java.sql.Types.OTHER) } }

        implicit object GetUUIDOption extends GetResult[Option[java.util.UUID]] { def apply(rs: PositionedResult) = rs.nextStringOption map (uuid) }
        implicit object GetGender extends GetResult[domain.Gender] { def apply(rs: PositionedResult) = domain.Gender.withName(rs.nextString()) }
        implicit object GetAddressOption extends GetResult[Option[domain.AddressInfo]] { def apply(rs: PositionedResult) = conversions.jdbc.addressInfoTypeMapper.nextOption(rs) }
        implicit object GetContacts extends GetResult[domain.Contacts] { def apply(rs: PositionedResult) = conversions.jdbc.contactsTypeMapper.nextValue(rs) }
      }

      def users(page: Int) = {
        import scala.slick.jdbc.{ StaticQuery => T }
        import T.interpolation

        import Convs._

        type Result = (Option[java.util.UUID], String, String, String, Long, Option[java.util.UUID], Option[Long], Option[Long], Option[java.util.UUID], domain.Gender, Option[domain.AddressInfo], Option[domain.AddressInfo], domain.Contacts, Option[String], Boolean, String)

        sql""" select
                 x2.x3, x2.x4, x2.x5, x2.x6, x2.x7, x2.x8, x2.x9, x2.x10, x2.x11, x2.x12, x2.x13, x2.x14, x2.x15, x2.x16, x2.x17, x3.label
               from (
                  select x18."id" as x3,
                         x18."primary_email" as x4,
                         x18."given_name" as x5,
                         x18."family_name" as x6,
                         x18."created_at" as x7,
                         x18."created_by" as x8,
                         x18."last_login_time" as x9,
                         x18."last_modified_at" as x10,
                         x18."last_modified_by" as x11,
                         x18."gender" as x12,
                         x18."home_address" as x13,
                         x18."work_address" as x14,
                         x18."contacts" as x15,
                         x18."avatar" as x16,
                         x18."change_password_at_next_login" as x17
                  from "users" x18 where not x18."_deleted" and x18."id" <> ${U.SuperUser.id} limit $MaxResults offset ${page * MaxResults}
                  order by last_modified_at desc nulls last, created_at desc
               ) x2 left join users_labels x3 on (x2.x3 = x3.user_id) group by x2.x3""".as[Result]
      }

      val trashedUsers = Compiled(for {
        u <- Users if u._deleted
      } yield (
        u.id,
        u.primaryEmail,
        u.givenName,
        u.familyName,
        u.createdAt,
        u.createdBy,
        u.lastLoginTime,
        u.lastModifiedAt,
        u.lastModifiedBy,
        u.gender,
        u.homeAddress,
        u.workAddress,
        u.contacts,
        u.avatar,
        u.changePasswordAtNextLogin))

      val userById = {

        def getUser(id: Column[java.util.UUID]) =
          for {
            u <- Users if !u._deleted && (u.id is id)
          } yield (
            u.id,
            u.primaryEmail,
            u.givenName,
            u.familyName,
            u.createdAt,
            u.createdBy,
            u.lastLoginTime,
            u.lastModifiedAt,
            u.lastModifiedBy,
            u.gender,
            u.homeAddress,
            u.workAddress,
            u.contacts,
            u.avatar,
            u.changePasswordAtNextLogin)

        Compiled(getUser _)
      }

      val tokenSecret = {
        def getTokenSecret(accessToken: Column[String]) =
          for {
            t <- OAuthTokens if t.accessToken is accessToken
          } yield t.macKey

        Compiled(getTokenSecret _)
      }

      val lastAccessTime = Compiled(OAuthTokens map (_.lastAccessTime))

      val refreshToken = {
        def getRefreshToken(mRefreshToken: Column[String]) =
          for {
            (t, c) <- OAuthTokens leftJoin OAuthClients on (_.clientId is _.id) if t.refreshToken is mRefreshToken
          } yield (
            t.accessToken,
            t.clientId,
            c.redirectUri,
            t.userId,
            t.refreshToken,
            t.macKey,
            t.uA,
            t.expiresIn,
            t.refreshExpiresIn,
            t.createdAt,
            t.lastAccessTime,
            t.scopes)

        Compiled(getRefreshToken _)
      }

      val forExchange = {
        def getRefreshToken(refreshToken: Column[String]) =
          for {
            (t, c) <- OAuthTokens leftJoin OAuthClients on (_.clientId is _.id) if t.refreshToken is refreshToken
          } yield (
            t.accessToken,
            t.clientId,
            c.redirectUri,
            t.userId,
            t.uA,
            t.refreshToken,
            t.createdAt,
            t.expiresIn,
            t.refreshExpiresIn,
            t.scopes)

        Compiled(getRefreshToken _)
      }

      val bearerToken = {
        def getAccessToken(accessToken: Column[String]) =
          OAuthTokens where (_.accessToken is accessToken)

        Compiled(getAccessToken _)
      }

      val accessToken = {
        def getAccessToken(mAccessToken: Column[String]) =
          for {
            (t, c) <- OAuthTokens leftJoin OAuthClients on (_.clientId is _.id) if t.accessToken is mAccessToken
          } yield (
            t.accessToken,
            t.clientId,
            c.redirectUri,
            t.userId,
            t.refreshToken,
            t.macKey,
            t.uA,
            t.expiresIn,
            t.refreshExpiresIn,
            t.createdAt,
            t.lastAccessTime,
            t.scopes)

        Compiled(getAccessToken _)
      }

      val userTokens = {
        def getUserTokens(userId: Column[java.util.UUID]) =
          for {
            t <- OAuthTokens if (t.userId is userId)
          } yield (
            t.accessToken,
            t.clientId,
            t.redirectUri,
            t.userId,
            t.refreshToken,
            t.macKey,
            t.uA,
            t.expiresIn,
            t.refreshExpiresIn,
            t.createdAt,
            t.lastAccessTime,
            t.scopes)

        Compiled(getUserTokens _)
      }

      val client = {
        def getClient(id: Column[String], secret: Column[String]) =
          for {
            c <- OAuthClients if (c.id is id) && (c.secret is secret)
          } yield (
            c.id,
            c.secret,
            c.redirectUri)

        Compiled(getClient _)
      }

      val session = {
        def getUserSession(bearerToken: Column[String], userAgent: Column[String]) =
          for {
            (u, t) <- Users leftJoin OAuthTokens on (_.id is _.userId)
            if /*(t.userId is uuid(userId)) &&
                         */ (t.accessToken is bearerToken) &&
              (t.uA is userAgent)
          } yield (u, (
            t.accessToken,
            t.clientId,
            t.refreshToken,
            t.macKey,
            t.uA,
            t.expiresIn,
            t.refreshExpiresIn,
            t.createdAt,
            t.lastAccessTime,
            t.scopes))

        Compiled(getUserSession _)
      }

      val auth = {
        def getUser(username: Column[String]) =
          for {
            u <- Users if !u._deleted && (u.primaryEmail is username)
          } yield (u.id, u.password)

        Compiled(getUser _)
      }

      val forLastLoginTime = {
        def getLastLoginTime(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (_.lastLoginTime)

        Compiled(getLastLoginTime _)
      }

      val primaryEmailExists = {
        def getPrimaryEmail(primaryEmail: Column[String]) =
          Query(Users where (_.primaryEmail.toLowerCase is primaryEmail) exists)

        Compiled(getPrimaryEmail _)
      }

      val forActivationKey = {
        def getActivationKey(username: Column[String]) =
          Users where (_.primaryEmail is username) map (o => (o.activationKey, o.suspended))

        Compiled(getActivationKey _)
      }

      val forActivation = {
        def getActivation(username: Column[String]) =
          Users where (_.primaryEmail is username) map (o => (o.activationKey, o.password, o.suspended))

        Compiled(getActivation _)
      }

      val userUpdates = {

        def forUsername_passwd_contacts(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.primaryEmail, o.password, o.contacts))

        def forPrimaryEmail(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.primaryEmail, o.lastModifiedAt, o.lastModifiedBy))

        def forPasswd(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.password, o.changePasswordAtNextLogin, o.lastModifiedAt, o.lastModifiedBy))

        def forFN(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.givenName, o.lastModifiedAt, o.lastModifiedBy))

        def forLN(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.familyName, o.lastModifiedAt, o.lastModifiedBy))

        def forGender(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.gender, o.lastModifiedAt, o.lastModifiedBy))

        def forHomeAddress(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.homeAddress, o.lastModifiedAt, o.lastModifiedBy))

        def forWorkAddress(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.workAddress, o.lastModifiedAt, o.lastModifiedBy))

        def forAvatar(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.avatar, o.lastModifiedAt, o.lastModifiedBy))

        def forContacts(id: Column[java.util.UUID]) =
          Users where (_.id is id) map (o => (o.contacts, o.lastModifiedAt, o.lastModifiedBy))

        new {
          val username_passwd_contacts = Compiled(forUsername_passwd_contacts _)
          val primaryEmail = Compiled(forPrimaryEmail _)
          val password = Compiled(forPasswd _)
          val givenName = Compiled(forFN _)
          val familyName = Compiled(forLN _)
          val gender = Compiled(forGender _)
          val homeAddress = Compiled(forHomeAddress _)
          val workAddress = Compiled(forWorkAddress _)
          val avatar = Compiled(forAvatar _)
          val contacts = Compiled(forContacts _)
        }
      }
    }

    def getUsersStats = {
      import Database.dynamicSession

      val num = Query(Users map (_.id) length)

      val result = db.withDynSession {
        num.firstOption
      }

      UsersStats(result getOrElse 0)
    }

    def getUsers(page: Int) = {
      import Database.dynamicSession

      val users = oq.users(page)

      val result = db.withDynSession {
        users.list
      }

      result.groupBy(_._1).flatMap {
        case (id, user :: rest) => User(user._2, None, user._3, user._4, user._5, user._6, user._7, user._8, user._9, user._10, user._11, user._12, user._13, user._14, changePasswordAtNextLogin = user._15, id = user._1, labels = user._16 :: rest.map(_._16)) :: Nil
      }.toList
    }

    def getUser(id: String) = {
      import Database.dynamicSession

      val user = oq.userById(uuid(id))

      val result = db.withDynSession {
        user.firstOption
      }

      result map {
        case (sId, primaryEmail, givenName, familyName, createdAt, createdBy, lastLoginTime, lastModifiedAt, lastModifiedBy, gender, homeAddress, workAddress, contacts, avatar, changePasswordAtNextLogin) =>
          User(primaryEmail, None, givenName, familyName, createdAt, createdBy, lastLoginTime, lastModifiedAt, lastModifiedBy, gender, homeAddress, workAddress, contacts, avatar, changePasswordAtNextLogin = changePasswordAtNextLogin, id = Some(sId), labels = labelService.getUserLabels(sId.toString) map (_.label))
      }
    }

    def removeUser(id: String) = {
      import Database.dynamicSession

      val q = Users.forDeletion(uuid(id))

      db.withDynSession {
        q.update(true) == 1
      }
    }

    def getPurgedUsers = {
      import Database.dynamicSession

      val trash = oq.trashedUsers

      val result = db.withDynSession {
        trash.list
      }

      result map {
        case (id, primaryEmail, givenName, familyName, createdAt, createdBy, lastLoginTime, lastModifiedAt, lastModifiedBy, gender, homeAddress, workAddress, contacts, avatar, changePasswordAtNextLogin) =>
          User(primaryEmail, None, givenName, familyName, createdAt, createdBy, lastLoginTime, lastModifiedAt, lastModifiedBy, gender, homeAddress, workAddress, contacts, avatar, changePasswordAtNextLogin = changePasswordAtNextLogin, id = Some(id))
      }
    }

    def purgeUsers(users: Set[String]) = {
      val q = for { u <- Users if (u.id isNot U.SuperUser.id) && (u.id inSet (users map uuid)) } yield u

      users foreach {
        id =>
          if (U.SuperUser.id exists (_.toString != id))
            oauthService.purgeAvatarForUser(id)
      }

      db.withTransaction { implicit sesssion =>
        q.delete
      }
    }

    def undeleteUsers(users: Set[String]) = {
      val deleted = for { u <- Users if u._deleted && (u.id inSet (users map uuid)) } yield u._deleted

      db.withTransaction { implicit sesssion =>
        deleted.update(false)
      }
    }

    def getTokenSecret(accessToken: String) = {
      import Database.dynamicSession

      val token = oq.tokenSecret(accessToken)

      db.withDynSession {
        token.firstOption
      }
    }

    def getRefreshToken(refreshToken: String) = {

      val token = oq.refreshToken(refreshToken)

      val result = db.withSession { implicit session =>
        token.firstOption
      }

      result flatMap {
        case (accessToken, clientId, redirectUri, userId, sRefreshToken, macKey, uA, expires, refreshExpires, createdAt, lastAccessTime, sScopes) =>

          def isExpired = refreshExpires exists (_ * 1000 + createdAt < System.currentTimeMillis)

          if (isExpired) {
            db withTransaction { implicit session =>
              if (oq.bearerToken(accessToken).delete != 1)
                throw new Exception("getRefreshToken: can't delete expired refresh token")
              None
            }
          } else Some(OAuthToken(accessToken, clientId, redirectUri, userId, sRefreshToken, macKey, uA, expires, refreshExpires, createdAt, lastAccessTime, scopes = sScopes))
      }
    }

    def exchangeRefreshToken(refreshToken: String) = db.withTransaction { implicit session =>

      val token = oq.forExchange(refreshToken)

      token.firstOption flatMap {
        case (aAccessToken, clientId, redirectUri, userId, uA, Some(aRefreshToken), issuedTime, expiresIn, refreshExpiresIn, aScopes) if refreshExpiresIn map (issuedTime + 1000 * _ > System.currentTimeMillis) getOrElse true => //aRefreshToken exists

          def generateToken = utils.Crypto.generateSecureToken // utils.SHA3 digest s"$clientId:$userId:${System.nanoTime}"
          def generateRefreshToken = utils.Crypto.generateSecureToken // utils.SHA3 digest s"$accessToken:$userId:${System.nanoTime}"
          def generateMacKey = utils.Crypto.genMacKey(s"$userId:${System.nanoTime}") // utils.genPasswd(s"$userId:${System.nanoTime}")

          val accessToken = generateToken

          val currentTimestamp = System.currentTimeMillis

          if ((OAuthTokens.forInsert +=
            (accessToken, clientId, redirectUri, userId, Some(generateRefreshToken), generateMacKey, uA, expiresIn, refreshExpiresIn, currentTimestamp, currentTimestamp, "mac", aScopes)) != 1)
            throw new Exception("could not refresh Token")

          val newToken = oq.accessToken(accessToken)

          newToken.firstOption map {
            case (sAccessToken, sClientId, sRedirectUri, sUserId, sRefreshToken, sMacKey, sUA, sExpires, sRefreshExpires, dCreatedAt, dLastAccessTime, dScopes) =>

              if (oq.bearerToken(aAccessToken).delete != 1)
                throw new Exception("couldn't delete old token")

              OAuthToken(sAccessToken, sClientId, sRedirectUri, sUserId, sRefreshToken, sMacKey, sUA, sExpires, sRefreshExpires, dCreatedAt, dLastAccessTime, scopes = dScopes)
          }

        case _ => None
      }
    }

    def revokeToken(accessToken: String) =
      db.withTransaction { implicit session =>
        val token = oq.tokenSecret(accessToken)
        token.delete == 1
      }

    def getUserTokens(userId: String) = {
      import Database.dynamicSession

      val userTokens = oq.userTokens(uuid(userId))

      val result = db.withDynSession {
        userTokens.list
      }

      result map {
        case (sAccessToken, sClientId, sRedirectUri, sUserId, sRefreshToken, sMacKey, sUA, sExpires, sRefreshExpires, sCreatedAt, sLastAccessTime, sScopes) =>
          OAuthToken(sAccessToken, sClientId, sRedirectUri, sUserId, sRefreshToken, sMacKey, sUA, sExpires, sRefreshExpires, sCreatedAt, sLastAccessTime, scopes = sScopes)
      }
    }

    def getUserSession(params: Map[String, String]) =
      db.withTransaction { implicit s =>
        //      val userId = params("userId")
        val bearerToken = params("bearerToken")
        val userAgent = params("userAgent")

        val session = oq.session(bearerToken, userAgent)

        session.firstOption map {
          case (sUser, (sAccessToken, sClientId, sRefreshToken, sMacKey, sUA, sExpiresIn, sRefreshExpiresIn, sCreatedAt, sLastAccessTime, sScopes)) =>

            import scala.util.control.Exception.allCatch

            allCatch.opt {
              oq.lastAccessTime update (System.currentTimeMillis)
            } // Touch session

            val userId = sUser.id map (_.toString) get

            val isSuperuser = sUser.isSuperuser

            Session(
              sAccessToken,
              sMacKey,
              sClientId,
              sCreatedAt,
              sExpiresIn,
              sRefreshExpiresIn,
              sRefreshToken,
              sLastAccessTime,
              user = sUser copy (password = None),
              userAgent = sUA,
              hasRole = Map(accessControlService.getRoles.par map (r => (r.name, isSuperuser || accessControlService.userHasRole(userId, r.name))) seq: _*) withDefaultValue (false),
              hasPermission = {
                val userPermissions = if (isSuperuser) None else Some { accessControlService.getUserPermissions(userId) } // TODO: is this dependency safe
                Map(accessControlService.getPermissions map (p => (p.name, isSuperuser || (userPermissions exists (_.contains(p.name))))): _*) withDefaultValue (false)
              },
              scopes = sScopes)
        }
      }

    def getClient(id: String, secret: String) = {
      import Database.dynamicSession

      val client = oq.client(id, secret)

      val result = db.withDynSession {
        client.firstOption
      }

      result map {
        case (cId, cSecret, redirectUri) =>
          OAuthClient(cId, cSecret, redirectUri)
      }
    }

    def authUser(username: String, password: String) = {
      val user = oq.auth(username)

      val result = db.withSession { implicit session =>
        user.firstOption
      }

      result collect {
        case (id, sPasswd) if passwords verify (password, sPasswd) =>

          db.withTransaction { implicit session =>
            oq.forLastLoginTime(id) update (Some(System.currentTimeMillis))
          }

          id.toString
      }
    }

    def saveToken(accessToken: String, refreshToken: Option[String], macKey: String, uA: String, clientId: String, redirectUri: String, userId: String, expiresIn: Option[Long], refreshExpiresIn: Option[Long], scopes: Set[String]) = {
      val currentTimestamp = System.currentTimeMillis

      if (db.withTransaction { implicit session =>
        (OAuthTokens.forInsert += (accessToken, clientId, redirectUri, uuid(userId), refreshToken, macKey, uA, expiresIn, refreshExpiresIn, currentTimestamp, currentTimestamp, "mac", scopes)) != 1
      }) throw new IllegalStateException("can't save token")

      val token = oq.accessToken(accessToken)

      val result = db.withSession { implicit session =>
        token.firstOption
      }

      result map {
        case (sAccessToken, sClientId, sRedirectUri, sUserId, sRefreshToken, sMacKey, sUA, sExpires, sRefreshExpires, sCreatedAt, sLastAccessTime, sScopes) =>
          OAuthToken(sAccessToken, sClientId, sRedirectUri, sUserId, sRefreshToken, sMacKey, sUA, sExpires, sRefreshExpires, sCreatedAt, sLastAccessTime, scopes = sScopes)
      }
    }

    def saveUser(primaryEmail: String, password: String, givenName: String, familyName: String, createdBy: Option[String], gender: domain.Gender, homeAddress: Option[domain.AddressInfo], workAddress: Option[domain.AddressInfo], contacts: domain.Contacts, changePasswordAtNextLogin: Boolean)(implicit system: akka.actor.ActorSystem) =
      db.withTransaction { implicit session =>
        import scala.util.control.Exception.allCatch

        val currentTimestamp = System.currentTimeMillis

        allCatch.opt {

          try

            Users insert (
              primaryEmail,
              passwords crypt password,
              givenName,
              familyName,
              currentTimestamp,
              createdBy map uuid,
              Some(currentTimestamp),
              createdBy map uuid,
              gender,
              homeAddress,
              workAddress,
              contacts,
              changePasswordAtNextLogin)

          catch {
            case ex: Throwable =>

              log.info(s"[saveUser failed with $ex]")
              throw ex
          }

          finally {
            utils.Mailer.sendWelcomeEmail(primaryEmail, password)
          }
        }
      }

    def updateUser(id: String, spec: UserSpec)(implicit system: akka.actor.ActorSystem) = {
      val uid = uuid(id)
      val username_passwd_contacts = oq.userUpdates.username_passwd_contacts(uid)

      db.withSession {
        implicit session => username_passwd_contacts.firstOption
      } match {

        case Some((sUsername, sPassword, sContacts)) =>
          db.withTransaction {
            implicit session =>

              val currentTimestamp = Some(System.currentTimeMillis)

              val _1 = spec.primaryEmail map {
                primaryEmail =>
                  oq.userUpdates.primaryEmail(uid).update(primaryEmail, currentTimestamp, Some(uid)) == 1
              } getOrElse true

              val _2 = _1 && (spec.password map {
                password =>
                  spec.oldPassword.nonEmpty &&
                    (passwords verify (spec.oldPassword.get, sPassword)) &&
                    (oq.userUpdates.password(uid).update(passwords crypt password, false, currentTimestamp, Some(uid)) == 1) &&
                    { utils.Mailer.sendPasswordChangedNotice(sUsername); true }
              } getOrElse true)

              val _3 = _2 && (spec.givenName map {
                givenName =>
                  oq.userUpdates.givenName(uid).update(givenName, currentTimestamp, Some(uid)) == 1
              } getOrElse true)

              val _4 = _3 && (spec.familyName map {
                familyName =>
                  oq.userUpdates.familyName(uid).update(familyName, currentTimestamp, Some(uid)) == 1
              } getOrElse true)

              val _5 = _4 && (spec.gender map {
                gender =>
                  oq.userUpdates.gender(uid).update(gender, currentTimestamp, Some(uid)) == 1
              } getOrElse true)

              val _6 = _5 && (spec.homeAddress foreach {
                case homeAddress =>
                  oq.userUpdates.homeAddress(uid).update(homeAddress, currentTimestamp, Some(uid)) == 1
              })

              val _7 = _6 && (spec.workAddress foreach {
                case workAddress =>
                  oq.userUpdates.workAddress(uid).update(workAddress, currentTimestamp, Some(uid)) == 1
              })

              val _8 = _7 && (spec.avatar foreach {
                case avatarId =>
                  //  avatars ! utils.Avatars.Save(id, avatar, data)
                  oq.userUpdates.avatar(uid).update(avatarId, currentTimestamp, Some(uid)) == 1
              })

              _8 && {

                spec.contacts map {
                  case s =>

                    import utils.If

                    val Contacts(MobileNumbers(curMobile1, curMobile2), curHome, curWork) = sContacts

                    val newHome = if (s.home eq None) curHome else Some {
                      val tmp = s.home.get
                      val empt = curHome.nonEmpty

                      ContactInfo(
                        email = If(tmp.email.set eq None, If(empt, curHome.get.email, None), tmp.email.set.get),
                        fax = If(tmp.fax.set eq None, If(empt, curHome.get.fax, None), tmp.fax.set.get),
                        phoneNumber = If(tmp.phoneNumber.set eq None, If(empt, curHome.get.phoneNumber, None), tmp.phoneNumber.set.get))
                    }

                    val newWork = if (s.work eq None) curWork else Some {
                      val tmp = s.work.get
                      val empt = curWork.nonEmpty

                      ContactInfo(
                        email = If(tmp.email.set eq None, If(empt, curHome.get.email, None), tmp.email.set.get),
                        fax = If(tmp.fax.set eq None, If(empt, curHome.get.fax, None), tmp.fax.set.get),
                        phoneNumber = If(tmp.phoneNumber.set eq None, If(empt, curHome.get.phoneNumber, None), tmp.phoneNumber.set.get))
                    }

                    val qContacts = oq.userUpdates.contacts(uid)

                    (qContacts update ((
                      Contacts(
                        mobiles = MobileNumbers(If(s.mobiles.mobile1.set eq None, curMobile1, s.mobiles.mobile1.set.get), If(s.mobiles.mobile2.set eq None, curMobile2, s.mobiles.mobile2.set.get)),
                        home = newHome,
                        work = newWork),
                        currentTimestamp,
                        Some(uid)))) == 1

                } getOrElse true
              }

          }

        case _ => false
      }
    }

    def primaryEmailExists(primaryEmail: String) = {
      import Database.dynamicSession

      val primaryEmailExists = oq.primaryEmailExists(primaryEmail.toLowerCase)

      db.withDynSession {
        primaryEmailExists.firstOption
      } getOrElse false
    }

    def createPasswdResetReq(username: String)(implicit system: akka.actor.ActorSystem) = db.withTransaction { implicit session =>
      val key = utils.Crypto.generateSecureToken

      val user = oq.forActivationKey(username)

      if (user.update(Some(utils.genPasswd(key)), true /* Suspend account */ ) == 1) utils.Mailer.sendPasswordResetEmail(username, key)
      else throw new Exception("createPasswdResetReq: can not update user_activation_key")
    }

    def checkActivationReq(username: String, ky: String) = {
      val user = oq.forActivationKey(username)

      val result = db.withSession { implicit session =>
        user.firstOption
      }

      result match {
        case Some((Some(hashed), _)) => passwords verify (ky, hashed)
        case _                       => false
      }
    }

    def resetPasswd(username: String, ky: String, newPasswd: String)(implicit system: akka.actor.ActorSystem) = db.withTransaction { implicit session =>
      val user = oq.forActivation(username)

      val result = db.withSession { implicit session =>
        user.firstOption
      }

      result match {
        case Some((Some(hashed), _, _)) if passwords verify (ky, hashed) =>

          utils.If(
            user.update(None, passwords crypt newPasswd, false /* Enable account */ ) == 1,
            { utils.Mailer.sendPasswordChangedNotice(username); true },
            false)

        case _ => false
      }
    }

    def getPage(userId: String) = {
      import scala.slick.jdbc.{ StaticQuery => T }
      import T.interpolation

      val page = sql""" SELECT (row_number() OVER () - 1) / $MaxResults as page FROM users WHERE id = CAST($userId AS UUID); """.as[Int]

      db.withSession { implicit session =>
        page.firstOption getOrElse 0
      }
    }
  }
}