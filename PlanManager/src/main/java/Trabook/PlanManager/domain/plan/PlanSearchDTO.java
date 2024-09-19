package Trabook.PlanManager.domain.plan;

import lombok.Getter;
import lombok.Setter;

@Getter

public class PlanSearchDTO {
    private String keyword;
    private Filters filters;
    private String sorts;

    private PlanSearchDTO() {}
    //why static?  중첩 클래스 vs 내부 클래스 공부하기
    //Filters 클래스는 해당 dto의 알부이지만 강하게 결합될 필요가 없다. 물리적으로 독립된 객체로 존재하는 것이 명확한 구조를 만든다.
    //외부클래스의 인스턴스를 생성하지 않고도 Filters 객체를 간단히 만들 수 있따.
    @Getter
    @Setter
    public static class Filters {
        private String region;
        private int memberCount;
        private int duration;
    }

}
