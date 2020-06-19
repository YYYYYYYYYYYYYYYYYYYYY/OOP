package ru.nsu.fit.oop.yaroslavodintsov.task_2_1_1.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.nsu.fit.oop.yaroslavodintsov.task_2_1_1.Data;
import ru.nsu.fit.oop.yaroslavodintsov.task_2_1_1.warehouse.Order;
import ru.nsu.fit.oop.yaroslavodintsov.task_2_1_1.warehouse.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class DeliveryGuy implements Runnable {

    private static final int WAITING_TIME_MILLISECONDS = 1000;

    @JsonProperty("id")
    private final int id;

    @JsonProperty("howManyCanDrag")
    private final int numOfPizzasCanCarry;

    private final List<Order> bag;

    @JsonProperty("deliveryTime")
    private final int deliveryTime;

    private Storage storage;
    private Data data;
    private Delivery deliveryWorkers;

    DeliveryGuy(
            @JsonProperty("id") int id,
            @JsonProperty("deliveryTime") int deliveryTime,
            @JsonProperty("howManyCanDrag") int numOfPizzasCanCarry) {
        this.id = id;
        this.deliveryTime = deliveryTime;
        this.numOfPizzasCanCarry = numOfPizzasCanCarry;
        this.bag = new ArrayList<>();

    }

    void setStorage(Storage storage) {

        this.storage = storage;

    }

    void setPizzaRestaurantHeadquarters(Data data) {

        this.data = data;

    }

    void setDeliveryWorkers(Delivery delivery) {

        this.deliveryWorkers = delivery;

    }

    @Override
    public void run() {

        while (!data.isRestaurantClosed()
                || !(storage.numOfItemsInWarehouse() == 0
                && data.areAllPizzaChefsFinishedWork())) {

            boolean isClosed = false;

            deliveryWorkers.lock.lock();
            try {

                if (data.isRestaurantClosed()
                        && storage.numOfItemsInWarehouse() == 0
                        && data.areAllPizzaChefsFinishedWork()) {

                    break;

                }

                for (int i = 0; i < numOfPizzasCanCarry; i++) {

                    Order order = null;

                    if (bag.size() != 0) {

                        try {

                            order = storage.pickItemForDelivery(WAITING_TIME_MILLISECONDS);

                            if (order == null) {

                                System.out.println(
                                        "DELIVERY GUY #"
                                                + id
                                                + " ONTHE WAY");
                                break;

                            }

                            bag.add(order);
                            System.out.println(
                                    "DELIVERY GUY #"
                                            + id
                                            + " PICKED ORDER #"
                                            + order.getId()
                                            + ". HAS "
                                            + bag.size()
                                            + " IN THE BAG.");

                        }

                        catch (InterruptedException e) {

                            e.printStackTrace();

                        }
                    }

                    else {

                        while (bag.size() == 0) {

                            if (data.isRestaurantClosed()
                                    && storage.numOfItemsInWarehouse() == 0
                                    && data.areAllPizzaChefsFinishedWork()) {

                                System.out.println(
                                        "CLOSED!");

                                isClosed = true;
                                break;

                            }

                            order = storage.pickItemForDelivery(WAITING_TIME_MILLISECONDS);

                            if (order != null) {

                                bag.add(order);

                            }

                        }

                        if (isClosed) {

                            break;

                        }

                        System.out.println(
                                "DELIVERY GUY #"
                                        + id
                                        + " PICKED UP ORDER #"
                                        + Objects.requireNonNull(order).getId()
                                        + ". HAS "
                                        + bag.size()
                                        + " IN THE BAG.");

                    }

                }

            }

            catch (InterruptedException e) {

                e.printStackTrace();

            }

            finally {

                deliveryWorkers.lock.unlock();

            }

            if (isClosed) {

                break;

            }

            System.out.println("DELIVERY GUY #" + id + " DONE");

            try {

                for (Order order : bag) {

                    Thread.sleep(deliveryTime);
                    data.completeOrder();
                    System.out.println(
                            "DELIVERY GUY #" + id + " COMPLETED ORDER");

                }

                System.out.println("DELIVERY GUY #" + id + " FINISHED ALL");
                Thread.sleep(deliveryTime);
                bag.clear();

            }

            catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

        data.endShiftForDeliveryWorker();
        System.out.println("DELIVERY GUY #" + id + " ALL 4 TODAY");
    }

    int getId() {

        return id;

    }

    int getDeliveryTime() {

        return deliveryTime;

    }

    int getNumOfPizzasCanCarry() {

        return numOfPizzasCanCarry;

    }
}