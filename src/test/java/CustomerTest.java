import hello.Application;
import hello.Customer;
import hello.CustomerRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CustomerTest {

    private static final Logger log  = LoggerFactory.getLogger(CustomerTest.class);

    @Autowired
    private CustomerRepository repository;

    @Before
    public void setUp(){
        this.repository.save(new Customer("null", "chen"));
    }

    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void testOptimisticLock() {
        Long id = 1L;
        Customer first = this.repository.findById(id).orElse(null);
        log.info(String.format("first touch, %s", first.toString()));
        first.setFirstName("Leon");

        Customer second = this.repository.findById(id).orElse(null);
        log.info(String.format("second touch, %s", second.toString()));
        second.setFirstName("Liang");

        this.repository.save(second);
        this.repository.save(first);
    }

    @Test
    public void testNoOptimisticLock() {
        Long id = 1L;
        Customer first = this.repository.findById(id).orElse(null);
        log.info(String.format("first touch, %s", first.toString()));
        first.setFirstName("Leon");

        this.repository.save(first);

        Customer second = this.repository.findById(id).orElse(null);
        log.info(String.format("second touch, %s", second.toString()));
        second.setFirstName("Liang");

        this.repository.save(second);

        Customer verified = this.repository.findById(id).orElse(null);
        log.info(String.format("final, %s", verified.toString()));
        assertThat(verified.getFirstName()).isEqualTo(second.getFirstName());
    }
}
