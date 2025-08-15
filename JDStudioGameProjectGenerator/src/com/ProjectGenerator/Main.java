package com.ProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
	
	private static final String REPO_URL = "https://github.com/JulioDaniel30/JavaGameEngineDC.git";
	private static final String OLD_PACKAGE_NAME = "com.game";
	
	public static void main(String[] args) {
		String sourcePath = "";
		Object[] sourceOptions = { "Clonar do GitHub", "Usar Pasta Local" };
		int sourceChoice = JOptionPane.showOptionDialog(null, "De onde virão os arquivos da engine e template?", "Fonte dos Arquivos", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sourceOptions, sourceOptions[0]);

		if (sourceChoice == 0) { // Clonar do GitHub
			String cloneDest = JOptionPane.showInputDialog(null, "Onde deseja salvar os arquivos da engine (pasta será criada)?", "Local para Clonar", JOptionPane.PLAIN_MESSAGE);
			if (cloneDest == null || cloneDest.trim().isEmpty()) return;
			
			File cloneDir = new File(cloneDest, "JavaGameEngineDC");
			if (!cloneRepository(REPO_URL, cloneDir)) {
				return; // Falha na clonagem, termina a execução
			}
			sourcePath = cloneDir.getAbsolutePath();
		} else if (sourceChoice == 1) { // Usar Pasta Local
			sourcePath = JOptionPane.showInputDialog(null, "Diretório fonte (contém a Engine e o Template):", "Fonte Local", JOptionPane.PLAIN_MESSAGE);
			if (sourcePath == null || sourcePath.trim().isEmpty()) return;
		} else {
			return; // Usuário fechou
		}
		
		String newProjectName = JOptionPane.showInputDialog(null, "Nome do novo projeto:", "Gerador de Projetos", JOptionPane.PLAIN_MESSAGE);
		if (newProjectName == null || newProjectName.trim().isEmpty()) return;

		String destPath = JOptionPane.showInputDialog(null, "Diretório de destino (onde o projeto será criado):", "Gerador de Projetos", JOptionPane.PLAIN_MESSAGE);
		if (destPath == null || destPath.trim().isEmpty()) return;
		
		String newPackageName = "";
		int renameChoice = JOptionPane.showConfirmDialog(null, "Deseja renomear o pacote padrão ('" + OLD_PACKAGE_NAME + "')?", "Renomear Pacote", JOptionPane.YES_NO_OPTION);
		if (renameChoice == JOptionPane.YES_OPTION) {
			newPackageName = JOptionPane.showInputDialog(null, "Digite o novo nome do pacote (ex: com.meuestudio.meujogo):", "Novo Pacote", JOptionPane.PLAIN_MESSAGE);
			if (newPackageName == null || newPackageName.trim().isEmpty()) {
				newPackageName = OLD_PACKAGE_NAME; // Se cancelar, usa o padrão
			}
		} else {
			newPackageName = OLD_PACKAGE_NAME; // Se não quiser, usa o padrão
		}

		Object[] engineOptions = { "Copiar Engine (Autocontido)", "Linkar com Engine (Workspace)" };
		int engineChoice = JOptionPane.showOptionDialog(null, "Como a engine deve ser tratada?", "Opção da Engine",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, engineOptions, engineOptions[0]);
		if (engineChoice == -1) return;
		int engineOption = engineChoice + 1;

		File newProjectDir = new File(destPath, newProjectName);
		if (newProjectDir.exists()) {
			JOptionPane.showMessageDialog(null, "Erro: O diretório do projeto '" + newProjectDir.getAbsolutePath() + "' já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		newProjectDir.mkdirs();

		generateProject(newProjectName, newProjectDir, sourcePath, engineOption, newPackageName);
		
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

	public static void generateProject(String newProjectName, File newProjectDir, String sourcePath, int engineOption, String newPackageName) {
		try {
			// A pasta do template pode ter um nome diferente no repositório
			Path sourceTemplatePath = Paths.get(sourcePath, "Game_Project_Template");
			if (!Files.exists(sourceTemplatePath)) { // Fallback para o nome antigo
				sourceTemplatePath = Paths.get(sourcePath, "Game");
			}
			
			Path destProjectPath = newProjectDir.toPath();

			// 1. Copia o template
			copyDirectory(sourceTemplatePath, destProjectPath);
			System.out.println("Template copiado.");

			// 2. Renomeia o pacote, se necessário
			if (!newPackageName.equals(OLD_PACKAGE_NAME)) {
				renamePackage(destProjectPath, OLD_PACKAGE_NAME, newPackageName);
			}

			// 3. Lida com a engine
			String engineFolderName = "com.JDStudio.Engine";
			Path sourceEnginePath = Paths.get(sourcePath, engineFolderName);
			if (!Files.exists(sourceEnginePath)) { // Fallback para o nome antigo
				sourceEnginePath = Paths.get(sourcePath, "Game_engine");
			}

			if (engineOption == 1) {
				Path destEnginePath = newProjectDir.getParentFile().toPath().resolve(engineFolderName);
				copyDirectory(sourceEnginePath, destEnginePath);
				System.out.println("Engine copiada.");
			}

			// 4. Modifica os metadados do Eclipse
			File projectFile = new File(newProjectDir, ".project");
			modifyProjectFile(projectFile, newProjectName);
			System.out.println("Arquivo .project modificado.");

			File classpathFile = new File(newProjectDir, ".classpath");
			modifyClasspathFile(classpathFile, engineFolderName);
			System.out.println("Arquivo .classpath modificado.");

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu um erro na geração:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	public static boolean cloneRepository(String repoUrl, File destination) {
	    System.out.println("Clonando repositório de " + repoUrl + "...");
	    try (Git result = Git.cloneRepository()
	            .setURI(repoUrl)
	            .setDirectory(destination)
	            .setCloneAllBranches(false) // Otimização: clona apenas o branch principal
	            .setBranch("main") // Ou "master", dependendo do seu repositório
	            .call()) {
	        System.out.println("Repositório clonado com sucesso para: " + result.getRepository().getDirectory());
	        return true;
	    } catch (GitAPIException e) {
	        JOptionPane.showMessageDialog(null, "Falha ao clonar o repositório:\n" + e.getMessage(), "Erro de Git", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	        return false;
	    }
	}
	public static void renamePackage(Path projectRoot, String oldPackageName, String newPackageName) throws IOException {
	    System.out.println("Renomeando pacote de '" + oldPackageName + "' para '" + newPackageName + "'...");

	    String oldPackagePath = oldPackageName.replace('.', File.separatorChar);
	    String newPackagePath = newPackageName.replace('.', File.separatorChar);

	    Path oldDir = projectRoot.resolve("src").resolve(oldPackagePath);
	    Path newDir = projectRoot.resolve("src").resolve(newPackagePath);

	    if (!Files.exists(oldDir)) {
	        System.err.println("Aviso: Pacote antigo '" + oldDir + "' não encontrado. Pulando renomeação.");
	        return;
	    }

	    // Garante que o novo diretório de destino exista
	    Files.createDirectories(newDir);

	    // Lista todos os arquivos .java no diretório antigo
	    List<Path> javaFiles = Files.walk(oldDir)
	                                .filter(path -> path.toString().endsWith(".java"))
	                                .collect(Collectors.toList());

	    // Modifica e move cada arquivo
	    for (Path sourceFile : javaFiles) {
	        String content = new String(Files.readAllBytes(sourceFile), StandardCharsets.UTF_8);
	        String modifiedContent = content.replace("package " + oldPackageName + ";", "package " + newPackageName + ";");
	        
	        // Determina o novo caminho do arquivo
	        Path destFile = newDir.resolve(oldDir.relativize(sourceFile));
	        
	        Files.write(destFile, modifiedContent.getBytes(StandardCharsets.UTF_8));
	    }
	    
	    // Apaga a estrutura de pastas antiga (de trás para a frente)
	    Files.walk(oldDir)
	        .sorted(Comparator.reverseOrder())
	        .map(Path::toFile)
	        .forEach(File::delete);

	    System.out.println("Renomeação de pacote concluída.");
	}
}