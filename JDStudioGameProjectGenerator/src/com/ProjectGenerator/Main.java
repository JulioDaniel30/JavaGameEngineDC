package com.ProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
public class Main {
	
	private static final String REPO_URL = "https://github.com/JulioDaniel30/JavaGameEngineDC.git";
	private static final String OLD_PACKAGE_NAME = "com.game";
	
	public static void main(String[] args) {

		String sourcePath = ""; // Esta variável guardará o caminho da fonte, seja ela local ou clonada.

		// --- ETAPA DE ESCOLHA DA FONTE ---
		Object[] sourceOptions = { "Clonar do GitHub", "Usar Pasta Local" };
		int sourceChoice = JOptionPane.showOptionDialog(null, 
				"De onde virão os arquivos da engine e template?", 
				"Fonte dos Arquivos", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, sourceOptions, sourceOptions[0]);

		if (sourceChoice == 0) { // Opção: Clonar do GitHub
			
			// Usando JFileChooser para escolher a pasta de destino do clone
			JOptionPane.showMessageDialog(null, "A seguir, escolha uma pasta onde o repositório da engine será baixado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
			JFileChooser cloneChooser = new JFileChooser();
			cloneChooser.setDialogTitle("Escolha onde salvar o repositório");
			cloneChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			cloneChooser.setAcceptAllFileFilterUsed(false);

			if (cloneChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File cloneDest = cloneChooser.getSelectedFile();
				File cloneDir = new File(cloneDest, "JavaGameEngineDC");
				
				List<String> foldersToCheckout = Arrays.asList("com.JDStudio.Engine", "Game_Project_Template", "Documentaçao");
				
				if (!cloneRepositoryWithSparseCheckout(REPO_URL, cloneDir, foldersToCheckout)) {
					return; // A clonagem falhou, o programa termina.
				}
				sourcePath = cloneDir.getAbsolutePath(); // A fonte agora é a pasta recém-clonada.
			} else {
				return; // Usuário cancelou o seletor de pastas
			}
			
		} else if (sourceChoice == 1) { // Opção: Usar Pasta Local
			
			// Usando JFileChooser para escolher a pasta local existente
			JOptionPane.showMessageDialog(null, "A seguir, escolha a sua pasta local do repositório.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
			JFileChooser sourceChooser = new JFileChooser();
			sourceChooser.setDialogTitle("Escolha a pasta fonte do repositório");
			sourceChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			if (sourceChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				sourcePath = sourceChooser.getSelectedFile().getAbsolutePath();
			} else {
				return; // Usuário cancelou
			}
			
		} else {
			return; // Usuário fechou a janela de opção inicial.
		}
		
		// --- O RESTO DO PROCESSO PARA CRIAR O PROJETO ---
		
		String newProjectName = JOptionPane.showInputDialog(null, "Nome do novo projeto:", "Gerador de Projetos", JOptionPane.PLAIN_MESSAGE);
		if (newProjectName == null || newProjectName.trim().isEmpty()) return;

		// Usando JFileChooser para o diretório de destino do novo projeto
		JOptionPane.showMessageDialog(null, "A seguir, escolha a pasta onde seu novo projeto será criado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
		JFileChooser destChooser = new JFileChooser();
		destChooser.setDialogTitle("Escolha onde salvar o novo projeto");
		destChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		String destPath = "";
		if (destChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			destPath = destChooser.getSelectedFile().getAbsolutePath();
		} else {
			return; // Usuário cancelou
		}
		
		String newPackageName = "";
		int renameChoice = JOptionPane.showConfirmDialog(null, "Deseja renomear o pacote padrão ('" + OLD_PACKAGE_NAME + "')?", "Renomear Pacote", JOptionPane.YES_NO_OPTION);
		if (renameChoice == JOptionPane.YES_OPTION) {
			newPackageName = JOptionPane.showInputDialog(null, "Digite o novo nome do pacote (ex: com.meuestudio.meujogo):", "Novo Pacote", JOptionPane.PLAIN_MESSAGE);
			if (newPackageName == null || newPackageName.trim().isEmpty()) {
				newPackageName = OLD_PACKAGE_NAME;
			}
		} else {
			newPackageName = OLD_PACKAGE_NAME;
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
	
	/**
	 * Clona um repositório Git baixando apenas as pastas especificadas,
	 * usando o método addPath da API JGit.
	 */
	public static boolean cloneRepositoryWithSparseCheckout(String repoUrl, File destination, List<String> foldersToCheckout) {
	    System.out.println("Clonando repositório de " + repoUrl + " (apenas pastas selecionadas)...");
	    
	    try (Git git = Git.cloneRepository()
	            .setURI(repoUrl)
	            .setDirectory(destination)
	            .setNoCheckout(true) // Clona apenas o .git, deixa a pasta de trabalho vazia
	            .call()) {

	        System.out.println("Repositório base clonado. Fazendo checkout das pastas especificadas...");

	        // --- A NOVA LÓGICA ESTÁ AQUI ---
	        // 1. Cria um comando de checkout focado no branch 'master'
	        CheckoutCommand checkout = git.checkout().setStartPoint("master");

	        // 2. Adiciona cada pasta que queremos baixar ao comando
	        for (String folder : foldersToCheckout) {
	            checkout.addPath(folder);
	        }

	        // 3. Executa o comando de checkout. Agora ele sabe exatamente quais pastas baixar.
	        checkout.call();

	        System.out.println("Checkout das pastas selecionadas concluído.");
	        System.out.println("Repositório clonado com sucesso para: " + destination.getAbsolutePath());
	        return true;
	        
	    } catch (GitAPIException e) {
	        JOptionPane.showMessageDialog(null, "Falha ao clonar o repositório:\n" + e.getMessage(), "Erro de Git", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	        return false;
	    }
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
	            .setBranch("master") // Ou "master", dependendo do seu repositório
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