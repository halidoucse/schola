package ma.epsilon.schola

package impl

trait LabelServicesComponentImpl extends LabelServicesComponent {
  this: LabelServicesRepoComponent =>

  class LabelServicesImpl extends LabelServices {

    def getLabels = labelServiceRepo.getLabels

    def updateLabel(label: String, newName: String) = labelServiceRepo.updateLabel(label, newName)

    def findOrNew(label: String, color: Option[String]) = labelServiceRepo.findOrNew(label, color)

    def remove(labels: Set[String]) {
      labelServiceRepo.remove(labels)
    }
  }
}

trait LabelServicesRepoComponentImpl extends LabelServicesRepoComponent {
  this: jdbc.WithDatabase =>

  import schema._
  import domain._
  import jdbc.Q._

  private[this] val log = Logger("oadmin.LabelServicesRepoComponentImpl")

  protected val labelServiceRepo = new LabelServicesRepoImpl

  class LabelServicesRepoImpl extends LabelServicesRepo {

    private[this] object oq {

      val labels = Compiled(Labels map (l => (l.name, l.color)))

      val labelled = {
        def getLabel(label: Column[String]) =
          Labels filter (_.name === label)

        Compiled(getLabel _)
      }

      val forUpdate = {
        def getLabelName(label: Column[String]) =
          Labels filter (_.name === label) map (_.name)

        Compiled(getLabelName _)
      }
    }

    def getLabels = {
      import Database.dynamicSession

      val labels = oq.labels

      val result = db.withDynSession {
        labels.list
      }

      result map (Label.tupled)
    }

    def updateLabel(label: String, newName: String) = {
      val labelInDB = oq.forUpdate(label)

      db.withTransaction { implicit session =>
        labelInDB.update(newName) == 1
      }
    }

    def findOrNew(label: String, color: Option[String] = None) = {

      val labelInDB = oq.labelled(label)

      def result = db.withSession { implicit session =>
        labelInDB.firstOption
      }

      result.fold {

        db.withTransaction { implicit session =>
          Option(Labels insert (label, color.getOrElse("#fff")))
        }

      } {
        case Label(name, colorInDB) =>

          for (c <- color if c != colorInDB)
            db.withTransaction { implicit session =>
              labelInDB.update(Label(label, c))
            }

          result
      }
    }

    def remove(labels: Set[String]) =
      db.withTransaction { implicit session =>
        val labelsInDB = Labels filter (_.name inSet labels)

        labelsInDB.delete
      }
  }
}