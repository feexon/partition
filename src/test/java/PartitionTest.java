import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Administrator
 * @version 1.0 2014/7/11,8:53
 *          partitionOn(even,[1,2,3,4])==2,items=[1,3,2,4]
 */
public class PartitionTest {

    private class PartitionAssertion {
        private final Object[] items;
        private final Predicate predicate;

        public PartitionAssertion(Object[] items, Predicate predicate) {
            this.items = items;
            this.predicate = predicate;
        }

        public void should(PartitionExpectation... expectations) {
            int actual = partitionOn(predicate, items);
            for (PartitionExpectation expectation : expectations) {
                expectation.checking(actual, items);
            }
        }


    }

    private interface PartitionExpectation {
        void checking(int actual, Object[] items);
    }

    private PartitionExpectation position(final int position) {
        return new PartitionExpectation() {
            public void checking(int actual, Object[] items) {
                assertThat(actual, equalTo(position));
            }
        };
    }

    private PartitionExpectation items(final Object... result) {
        return new PartitionExpectation() {
            public void checking(int actual, Object[] items) {
                assertThat(items, equalTo(result));
            }
        };
    }


    private PartitionAssertion partition(Predicate predicate, Object... items) {
        return new PartitionAssertion(items, predicate);
    }

    private Object[] with(Object... items) {
        return items;
    }

    private Predicate even() {
        return new Predicate() {
            public boolean apply(Object item) {
                return (Integer) item % 2 == 0;
            }
        };
    }

    @Test
    public void allEven() throws Exception {
        partition(even(), with(2, 4, 6, 8)).should(position(0), items(2, 4, 6, 8));
    }

    @Test
    public void allOdd() throws Exception {
        partition(even(), with(1, 3, 5, 7)).should(position(4), items(1, 3, 5, 7));
    }

    @Test
    public void partitioned() throws Exception {
        partition(even(), with(1, 3, 2, 4)).should(position(2), items(1, 3, 2, 4));
    }

    @Test
    public void transpose2() throws Exception {
        partition(even(), with(2, 1)).should(position(1), items(1, 2));
    }


    @Test
    public void transpose() throws Exception {
        partition(even(), with(4, 2, 3, 1)).should(position(2), items(3, 1, 4, 2));
        partition(even(), with(3, 2, 1, 4, 5, 7, 6)).should(position(4), items(3, 1, 5, 7, 2, 4, 6));
    }

    @Test
    public void once() throws Exception {
        Predicate once = new Predicate() {
            boolean requested = false;
            public boolean apply(Object item) {
                if (!requested) {
                    requested = true;
                    return true;
                }
                return false;
            }
        };
        partition(once, with(5, 1, 2, 3)).should(position(3), items(1, 2, 3, 5));
    }

    // source code
    private int partitionOn(Predicate predicate, Object[] items) {
        Object[] satisfied = new Object[items.length];
        int f = 0, t = 0;
        for (Object item : items) {
            if (predicate.apply(item)) {
                satisfied[t++] = item;
            } else {
                items[f++] = item;
            }
        }
        System.arraycopy(satisfied, 0, items, f, t);
        return f;
    }

    private interface Predicate {
        public boolean apply(Object item);
    }


}
