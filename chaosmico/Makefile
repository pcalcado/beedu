SBT = vendor/sbt/bin/sbt -Duser.home=$(HOME) -J-Xmx3G -J-Xms512m -J-XX\:+UseConcMarkSweepGC -J-XX\:+CMSClassUnloadingEnabled -J-XX\:MetaspaceSize=512M

all: pre-checkin

unit-test:
	 $(SBT) test

compile:
	 $(SBT) compile test:compile

pre-checkin:
	 $(SBT) clean test +publishLocal

publish:
	$(SBT) +publish

clean:
	$(SBT) +clean

interactive:
	$(SBT)
sbt:
	$(SBT) '$(COMMAND)'

release:
	$(SBT) release

