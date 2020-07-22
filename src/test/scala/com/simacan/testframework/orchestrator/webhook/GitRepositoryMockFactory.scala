package com.simacan.testframework.orchestrator.webhook

private[orchestrator] object GitRepositoryMockFactory {

  /**
    * Returns an initialized GitRepositoryMock
    * @return
    */
  def create(): GitRepositoryMock = new GitRepositoryMock().init()
}
