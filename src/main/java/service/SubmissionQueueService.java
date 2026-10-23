package service;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class SubmissionQueueService {
  // 1. enqueue - add submission to queue -
  // 2. StartBackgroundThread
  private final BlockingQueue<UUID> queue = new LinkedBlockingQueue<>();
  private final JudgeService judgeService;
  ExecutorService executor = Executors.newFixedThreadPool(3);

  @Inject
  public SubmissionQueueService(JudgeService judgeService) {
    this.judgeService = judgeService;
    startBackgroundThread();
  }

  public void enqueue(UUID submissionId) {
    queue.offer(submissionId);
    System.out.println("Enqueued submission: " + submissionId + " queue: " + queue.size());
  }

  public void startBackgroundThread() {

    executor.submit(
        () -> {
          while (!Thread.currentThread().isInterrupted()) {
            try {
              UUID submissionId = queue.take();
              System.out.println("Thread-ul "  + Thread.currentThread().getName() + " has submission: " + submissionId);

              judgeService.judgeSubmissionById(submissionId);

            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
          }
        });
  }
}
