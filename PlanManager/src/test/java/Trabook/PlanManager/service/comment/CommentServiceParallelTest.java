package Trabook.PlanManager.service.comment;

import Trabook.PlanManager.domain.comment.CommentRequestDTO;
import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.domain.plan.PlanCreateDTO;
import Trabook.PlanManager.repository.plan.PlanRepository;
import Trabook.PlanManager.response.CommentUpdateResponseDTO;
import Trabook.PlanManager.service.PlanService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
public class CommentServiceParallelTest {
    private static final int TOTAL_COUNT = 3;
    private Plan plan;
    private ExecutorService executorService;
    private CountDownLatch latch;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    PlanService planService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(TOTAL_COUNT);
        latch = new CountDownLatch(TOTAL_COUNT);
         plan =  planRepository.findById(580).get();

    }

    @Test
    void commentServiceParellelTest() throws Exception {
        for (int i = 0; i < TOTAL_COUNT; i++){
            int commentNumber = i + 1;
            executorService.submit(() ->  {
                try {
                    planService.addComment(new CommentRequestDTO(0,3,580,"testContent"+commentNumber,0,0,"2024-10-13T16:18:35.911Z"));
                } catch( Exception e){
                    System.out.println(e);
                } finally {
                    latch.countDown();

                }
            });
        }
        latch.await();

        Assertions.assertThat(planRepository.findById(580).get().getNumOfComments()).isEqualTo(TOTAL_COUNT);
    }

}
