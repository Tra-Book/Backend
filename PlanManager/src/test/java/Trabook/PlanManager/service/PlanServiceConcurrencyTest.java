package Trabook.PlanManager.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
@SpringBootTest

public class PlanServiceConcurrencyTest {
    @Autowired
    PlanService planService;


    @Test
    void updateLikeConcurrencyTest() throws InterruptedException{
        //given
        int likes = planService.getPlan(32).get().getLikes();
        //when

        int numOfThreads = 100;

        //쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

        CountDownLatch latch = new CountDownLatch(numOfThreads);

        for(int i=1;i<=numOfThreads;i++) {
            int finalI1 = i;
            executorService.submit(()-> {

                try{
                    System.out.println(finalI1 + "번째 쓰레드 접근 시작");
                    planService.deleteLike(3,32);

                } finally {
                    latch.countDown();
                    System.out.println(finalI1 + "번쨰 쓰레드 종료");
                }
            });
        }
        latch.await();
        executorService.shutdown();

        //then
        int likedResult = planService.getPlan(32).get().getLikes();

        Assertions.assertThat(likedResult).isEqualTo(600L);
    }
}


 */