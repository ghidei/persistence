# Description (sample taken from [akka-samples](https://github.com/akka/akka-samples/tree/2.5/akka-sample-main-scala))

This tutorial contains examples that illustrate a subset of [Akka Persistence](http://doc.akka.io/docs/akka/2.5/scala/persistence.html) features.

Custom storage locations for the journal and snapshots can be defined in [application.conf](src/main/resources/application.conf).

## Persistent actor

[PersistentActorExample.scala](src/main/scala/sample/persistence/PersistentActorExample.scala) is described in detail in the [Event sourcing](http://doc.akka.io/docs/akka/2.5/scala/persistence.html#event-sourcing) section of the user documentation. With every application run, the `ExamplePersistentActor` is recovered from events stored in previous application runs, processes new commands, stores new events and snapshots and prints the current persistent actor state to `stdout`.

To run this example, type `sbt "runMain sample.persistence.PersistentActorExample"`.

## To run [ldfi-akka](https://github.com/KTH/ldfi-akka)

1. Checkout fresh branch

	`git checkout -b <branch_name>`

2. Clone ldfi-akka to branch root

	`git clone https://github.com/KTH/ldfi-akka.git`

3. Add the following dependency to build.sbt

	```
	lazy val ldfiakka = (project in file ("ldfi-akka"))
	.settings(
		name := "ldfi-akka",
		mainClass in Compile := Some("ldfi.akka.Main"))
	.dependsOn(global)
	```
4. Compile project

	`sbt compile`

5. Copy code in to ldfi-akka

	`sbt "ldfiakka/runMain ldfi.akka.Main --copy src/main/scala"`

6. Compile ldfi-akka

	`(cd ldfi-akka; sbt compile)`

7. Rewrite code

	`sbt "ldfiakka/runMain ldfi.akka.Main --rewrite"`

6. Compile ldfi-akka

	`(cd ldfi-akka; sbt compile)`

9. Run ldfi-akka

	`sbt "ldfiakka/runMain ldfi.akka.Main -m src/main/scala/sample/persistence/PersistentActorExample.scala -v src/main/scala/sample/persistence/PersistentActorExample.scala verifyCorrectness"`