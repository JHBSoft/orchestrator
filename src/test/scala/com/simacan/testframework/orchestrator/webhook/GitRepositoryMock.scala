package com.simacan.testframework.orchestrator.webhook

import java.io.File
import java.nio.file.Files

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository

class GitRepositoryMock {

  lazy val remoteRepoPath: File =
    Files.createTempDirectory("GitTestRepository").toFile
  lazy val localRepoPath: File =
    Files.createTempDirectory("clone").toFile

  lazy val remoteRepository: Repository = createRemoteRepositoryFromFile(
    remoteRepoPath
  )

  lazy val localRepository: Repository =
    cloneRepositoryToFile(remoteRepository, localRepoPath)

  /**
    * When given a directory, deletes it recursively. When given a file, just deletes it.
    * @param directoryOrFile The directory or file to delete
    * @return true if the directory was deleted successfully, false otherwise (e.g. if the directory didn't exist to begin with)
    */
  private[this] def recursiveDelete(directoryOrFile: File): Boolean = {
    val filesInDir: Array[File] =
      Option(directoryOrFile.listFiles())
        .getOrElse(Array.empty[File]) // convert null result for files to empty array

    // Make sure to delete the internal files first. This dependency on side effects makes tail recursion impossible.
    val internalFilesDeleted: Boolean =
      filesInDir.forall(file => recursiveDelete(file))

    internalFilesDeleted && directoryOrFile.delete()
  }

  /**
    * Cleans up the directories created for this GitRepository Mock
    * @return true if the directories are cleaned up properly, otherwise false.
    */
  private[orchestrator] def cleanUp(): Boolean = {
    recursiveDelete(remoteRepoPath) & recursiveDelete(localRepoPath)
  }

  /**
    * Initializes the remote repository, adds a file to it and clones it.
    * @return the clone repository needed for testing pull requests
    */
  private[orchestrator] def init(): GitRepositoryMock = {

    this.stageAndCommitFileToRepository(
      remoteRepository,
      File.createTempFile("rem", "", remoteRepoPath),
      "MyFirstCommit"
    )

    // Initialize the clone of the remote repository as localRepo.
    localRepository

    this.stageAndCommitFileToRepository(
      remoteRepository,
      File.createTempFile("rem", "", remoteRepoPath),
      "MySecondCommit"
    )

    this
  }

  /**
    * Clones the given RemoteRepo to the clonePath
    * @param remoteRepo: Repository
    * @param clonePath: File
    * @return Repository newly created repository
    */
  private[this] def cloneRepositoryToFile(remoteRepo: Repository, clonePath: File): Repository = {
    // Create clone of the origin repo
    Git
      .cloneRepository()
      .setURI(remoteRepo.getDirectory.getAbsolutePath)
      .setDirectory(clonePath)
      .call()
      .getRepository
  }

  /**
    * Given a path, a remote repository is created at the position of path
    *
    * @param repositoryPath (File) path where to create the remote repository
    * @return
    */
  private[this] def createRemoteRepositoryFromFile(
    repositoryPath: File
  ): Repository = {
    Git.init().setDirectory(remoteRepoPath).call().getRepository
  }

  /**
    * Stages a file to a given Git Repository
    * @param repository repository to stage file to
    * @param file file to stage
    * @return
    */
  private[orchestrator] def stageFileToRepository(
    repository: Repository,
    file: File
  ): GitRepositoryMock = {
    new Git(repository).add.addFilepattern(file.getName).call()
    this
  }

  /**
    * Commit all staged files in the given repository
    * @param repository repository to commit staged files to
    * @param commitMessage commit message
    * @return
    */
  private[orchestrator] def commitFilesToRepository(
    repository: Repository,
    commitMessage: String = "MyCommit"
  ): GitRepositoryMock = {
    new Git(repository).commit
      .setMessage(commitMessage)
      .setAuthor("unit test", "unit@test")
      .call()
    this
  }

  /**
    * Stages and commits a file to a given repository using commitMessage in the commit.
    * @param repository Repository to commit to
    * @param file file to commit
    * @param commitMessage message to add to the commit
    * @return
    */
  private[orchestrator] def stageAndCommitFileToRepository(
    repository: Repository,
    file: File,
    commitMessage: String
  ): GitRepositoryMock = {

    this
      .stageFileToRepository(repository, file)
      .commitFilesToRepository(repository, commitMessage)

  }
}
