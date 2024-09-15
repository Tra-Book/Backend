package Trabook.PlanManager.service.destination;

import Trabook.PlanManager.domain.destination.DestinationReactionDto;
import Trabook.PlanManager.domain.destination.Place;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
/*
@Transactional
@SpringBootTest
class DestinationServiceTest {

    @Autowired
    DestinationService destinationService;

    @Test
    void getPlaceListByCity() {
    }

    @Test
    void getPlaceListByUserScrap() {
        //given
        long userId = 3;
        long userId2 = 4;
        DestinationReactionDto destinationReactionDto;
        destinationReactionDto = new DestinationReactionDto("SCRAP",3,1);
        String result = destinationService.addPlaceReaction(destinationReactionDto);

        //when
        List<Place> placeListByUserScrap = destinationService.getPlaceListByUserScrap(userId);
        List<Place> placeListByUserScrap1 = destinationService.getPlaceListByUserScrap(userId2);
        //then
        Assertions.assertThat(placeListByUserScrap).hasSize(1);
        Assertions.assertThat(placeListByUserScrap1).isEmpty();

    }
    @Test
    void addPlaceReaction() {
        //given
        long userId = 3;
        long placeId = 1;
        long before = destinationService.getPlace(placeId).get().getNumOfAdded();
        DestinationReactionDto destinationReactionDto;
        destinationReactionDto = new DestinationReactionDto("SCRAP",3,1);
        //when
        String result = destinationService.addPlaceReaction(destinationReactionDto);

        String result2 = destinationService.addPlaceReaction(destinationReactionDto);
        long after = destinationService.getPlace(placeId).get().getNumOfAdded();

        //then


        Assertions.assertThat(result).isEqualTo("scrap complete");
        Assertions.assertThat(result2).isEqualTo("already scrapped");
        Assertions.assertThat(after).isEqualTo(before+1);
        //q : insert는 한 트랜젝션 내에서 두개의 쿼리가 있을 때 두번 쨰 쿼리가 삽입 된 이후 로 인식 하는데
        // update는 한 트랜잭션 내에서 두개의 쿼리가 있을 때 첫번 쨰 쿼리가 커밋하기 전 데이터를 인식하는가


    }

    @Test
    void deletePlaceReaction() {
        //given
        DestinationReactionDto destinationReactionDto;
        destinationReactionDto = new DestinationReactionDto("SCRAP",3,1);
        destinationService.addPlaceReaction(destinationReactionDto);
        // when
        String result1 = destinationService.deletePlaceReaction(destinationReactionDto);
        String result2 = destinationService.deletePlaceReaction(destinationReactionDto);
        //then
        Assertions.assertThat(result1).isEqualTo("delete scrap complete");
        Assertions.assertThat(result2).isEqualTo("can't delete null");
    }

    @Test
    void getPlaceByPlaceId() {
        //given
        long placeId=1;
        //when
        Optional<Place> result = destinationService.getPlaceByPlaceId(placeId);
        Optional<Place> result2 = destinationService.getPlaceByPlaceId(-1);
        //then
        Assertions.assertThat(result.get().getPlaceId()).isEqualTo(placeId);
        Assertions.assertThat(result2).isEmpty();
    }

    @Test
    void getPlace() {
    }
}

 */