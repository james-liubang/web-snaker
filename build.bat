dir /src *.java > sources.txt
javac @sources.txt
jar cf app.jar com/flysky/*.class