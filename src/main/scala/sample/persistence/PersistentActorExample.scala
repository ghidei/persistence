package sample.persistence

//#persistent-actor-example
import akka.actor._
import akka.persistence._
import scala.concurrent.Await
import scala.concurrent.duration._


case class Cmd(data: String)

case class Evt(data: String)

case object setEvents

object globalEvents {
  var events: List[String] = List.empty
}

case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)

  def size: Int = events.length

  override def toString: String = events.reverse.toString
}

class ExamplePersistentActor extends PersistentActor with ActorLogging {
  override def persistenceId = "sample-id-1"

  var state = ExampleState()

  def updateState(event: Evt): Unit = state = state.updated(event)

  def numEvents: Int = state.size

  val receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  val receiveCommand: Receive =  {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snap" => saveSnapshot(state)
    case "print" => println(state)
    case setEvents => globalEvents.events = state.events
  }

}

//#persistent-actor-example

object PersistentActorExample extends App {

  val system = ActorSystem("example")

  val persistentActor = system.actorOf(Props[ExamplePersistentActor], "persistentActor-4-scala")

  persistentActor ! setEvents

  Thread.sleep(2000)

  val initState = globalEvents.events

  Thread.sleep(2000)

  persistentActor ! Cmd("foo")
  persistentActor ! Cmd("baz")
  persistentActor ! Cmd("bar")
  persistentActor ! "snap"
  persistentActor ! "print"
  persistentActor ! Cmd("buzz")

  Thread.sleep(5000)

  persistentActor ! setEvents

  Thread.sleep(2000)

  val endState = globalEvents.events


  println("initState: " + initState)
  println("endState: " + endState)

  println(verifyCorrectness())

  Thread.sleep(3000)
  Await.ready(system.terminate(), 60.seconds)



  def verifyCorrectness(): Boolean = {
    val (firstFour, _) = endState.splitAt(4)
    val correctList = List("foo-", "foo-", "baz-", "baz-")
    compareLists(firstFour.reverse, correctList, endState.length - 4)
  }

  def verifyCorrectnessOld(): Boolean = {
    val correctList = List("foo-", "foo-", "baz-", "baz-", "bar-", "bar-", "buzz-", "buzz-")
    initState match {
      case Nil => compareLists(endState.reverse, correctList, 0)
      case _ =>
        val groupedInitState = initState.grouped(8).toList.reverse
        val groupedEndState = endState.grouped(8).toList.reverse

        val isInitStateValid = verifyState(groupedInitState, correctList, 0)
        val isEndStateValid = verifyState(groupedEndState, correctList, 0)

        isInitStateValid && isEndStateValid
    }
  }

  def verifyState(listOfList: List[List[String]], correctList: List[String], count: Int): Boolean = listOfList match {
    case Nil => true
    case head :: tail => compareLists(head.reverse, correctList, count) && verifyState(tail, correctList, count + 8)
  }

  def compareLists(eventList: List[String], correctList: List[String], count: Int): Boolean =
    (eventList, correctList) match {
      case (Nil, Nil) => true
      case (ehead :: etail, chead :: ctail) => compareLists(etail, ctail, count + 1) && ehead == (chead + count)
      case _ => false
    }

}

