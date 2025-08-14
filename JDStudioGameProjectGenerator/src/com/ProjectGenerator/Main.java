package com.ProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane; // Importação necessária para a UI

public class Main {
	public static void main(String[] args) {
		// --- Lógica de UI com JOptionPane ---

		String newProjectName = JOptionPane.showInputDialog(null, "Nome do novo projeto:", "Gerador de Projetos", JOptionPane.PLAIN_MESSAGE);
		if (newProjectName == null || newProjectName.trim().isEmpty()) {
			return; // Usuário cancelou ou não digitou nada
		}

		String destPath = JOptionPane.showInputDialog(null, "Diretório de destino (ex: C:/Dev/MeusJogos):", "Gerador de Projetos", JOptionPane.PLAIN_MESSAGE);
		if (destPath == null || destPath.trim().isEmpty()) {
			return;
		}

		String sourcePath = JOptionPane.showInputDialog(null, "Diretório fonte (contém a Engine e o Template):", "Gerador de Projetos", JOptionPane.PLAIN_MESSAGE);
		if (sourcePath == null || sourcePath.trim().isEmpty()) {
			return;
		}

		Object[] options = { "Copiar Engine (Autocontido)", "Linkar com Engine (Workspace)" };
		int choice = JOptionPane.showOptionDialog(null, "Como a engine deve ser tratada?", "Opção da Engine",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (choice == -1) {
			return; // Usuário fechou a caixa de diálogo
		}
		int engineOption = choice + 1; // Mapeia a escolha (0 ou 1) para (1 ou 2)

		File newProjectDir = new File(destPath, newProjectName);
		if (newProjectDir.exists()) {
			JOptionPane.showMessageDialog(null, "Erro: O diretório do projeto '" + newProjectDir.getAbsolutePath() + "' já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		newProjectDir.mkdirs();

		generateProject(newProjectName, newProjectDir, sourcePath, engineOption);
		
		JOptionPane.showMessageDialog(null, "Projeto gerado com sucesso em:\n" + newProjectDir.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void copyDirectory(Path source, Path destination) throws IOException {
		Files.walk(source).forEach(sourcePath -> {
			try {
				Path destinationPath = destination.resolve(source.relativize(sourcePath));
				Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.err.println("Falha ao copiar " + sourcePath + ": " + e.getMessage());
			}
		});
	}

	public static void modifyProjectFile(File projectFile, String newName) throws IOException {
		String content = new String(Files.readAllBytes(projectFile.toPath()), StandardCharsets.UTF_8);
		content = content.replace("<name>Game_Project_Template</name>", "<name>" + newName + "</name>");
		Files.write(projectFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	public static void modifyClasspathFile(File classpathFile, String engineProjectName) throws IOException {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<classpath>\n"
				+ "    <classpathentry kind=\"src\" path=\"src\"/>\n"
                + "    <classpathentry kind=\"src\" path=\"res\"/>\n"
				+ "    <classpathentry kind=\"src\" path=\"/" + engineProjectName + "\"/>\n"
				+ "    <classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n"
				+ "    <classpathentry kind=\"output\" path=\"bin\"/>\n" + "</classpath>";
		Files.write(classpathFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	public static void generateProject(String newProjectName, File newProjectDir, String sourcePath, int engineOption) {
		try {
			Path sourceTemplatePath = Paths.get(sourcePath, "Game_Project_Template");
			Path destProjectPath = newProjectDir.toPath();

			// 1. Copia o template
			copyDirectory(sourceTemplatePath, destProjectPath);
			System.out.println("Template copiado.");

			// 2. Se a opção for copiar a engine, copia para ao lado do novo projeto
			if (engineOption == 1) {
				Path sourceEnginePath = Paths.get(sourcePath, "com.JDStudio.Engine");
				Path destEnginePath = newProjectDir.getParentFile().toPath().resolve("com.JDStudio.Engine");
				copyDirectory(sourceEnginePath, destEnginePath);
				System.out.println("Engine copiada.");
			}

			// 3. Modifica os metadados do Eclipse
			File projectFile = new File(newProjectDir, ".project");
			modifyProjectFile(projectFile, newProjectName);
			System.out.println("Arquivo .project modificado.");

			File classpathFile = new File(newProjectDir, ".classpath");
			modifyClasspathFile(classpathFile, "com.JDStudio.Engine"); // Nome do projeto da engine
			System.out.println("Arquivo .classpath modificado.");

		} catch (IOException e) {
			e.printStackTrace();
			// Mostra o erro numa janela também
			JOptionPane.showMessageDialog(null, "Ocorreu um erro:\n" + e.getMessage(), "Erro na Geração", JOptionPane.ERROR_MESSAGE);
		}
	}
}