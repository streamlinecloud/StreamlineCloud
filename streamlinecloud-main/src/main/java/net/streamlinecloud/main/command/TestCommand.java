package net.streamlinecloud.main.command;

import net.streamlinecloud.main.terminal.api.CloudCommand;

public class TestCommand extends CloudCommand {

    public TestCommand() {
        setName("test");
        setDescription("Test command for developers");
    }

    @Override
    public void execute(String[] args) {

        //Template template = new Template("testtemplate", TemplateEnums.SOFTWARE.SERVER);

        //if (!template.templateCreated()) template.createTemplate(false);

        /*
        // Pfade zur JAR-Datei und dem Serververzeichnis anpassen
        File serverJar = new File("/home/creperozelot/IdeaProjects/CloudSystem/src/test/server/paper-1.18.2-388.jar");
        File serverDirectory = new File("/home/creperozelot/IdeaProjects/CloudSystem/src/test/server");
        File javaExec = new File("/home/creperozelot/.sdkman/candidates/java/17.0.8-amzn/bin/java");

        // Erstelle einen Prozess-Builder mit dem JDK-Pfad
        ProcessBuilder processBuilder = new ProcessBuilder(javaExec.getAbsolutePath(), "-Xmx2G", "-jar", serverJar.getName());
        processBuilder.directory(serverDirectory);

        // Erstelle PipedInputStream und PipedOutputStream für die Server-Output-Kommunikation
        PipedInputStream serverOutput = new PipedInputStream();

        // Starte den Server in einem separaten Thread
        ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> {
            try {
                Process process = processBuilder.start();
                // Verbinde den Server-Output mit dem PipedOutputStream
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = inputReader.readLine()) != null) {
                    System.out.println("Server: " + line);
                }
                // Warte auf den Server, bis er beendet ist
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Minecraft-Server erfolgreich gestartet.");
                } else {
                    System.out.println("Minecraft-Server konnte nicht gestartet werden. Exit-Code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });


        // Beende den Server und den ExecutorService
        serverExecutor.shutdown();
         */
    }

}

