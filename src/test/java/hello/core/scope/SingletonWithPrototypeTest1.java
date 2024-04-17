package hello.core.scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCnt();
        assertThat(prototypeBean1.getCnt()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCnt();
        assertThat(prototypeBean2.getCnt()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int cnt1 = clientBean1.logic();
        assertThat(cnt1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int cnt2 = clientBean2.logic();
        assertThat(cnt2).isEqualTo(2);

        assertThat(clientBean1).isSameAs(clientBean2);

    }

    @Scope("prototype")
    static class PrototypeBean {
        private int cnt = 0;

        public void addCnt() {
            cnt++;
        }

        public int getCnt() {
            return cnt;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }

    @Scope("singleton") //생략 가능
    static class ClientBean {

        private final PrototypeBean prototypeBean;

        @Autowired
        public ClientBean(final PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCnt();
            return prototypeBean.getCnt();
        }
    }
}
