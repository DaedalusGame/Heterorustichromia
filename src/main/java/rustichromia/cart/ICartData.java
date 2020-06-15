package rustichromia.cart;

public interface ICartData {
    CartState getState();

    boolean isState(CartStateSupplier supplier);

    CartContent getContent();

    boolean isIncoming();

    boolean isStill();

    boolean isOutgoing();
}
