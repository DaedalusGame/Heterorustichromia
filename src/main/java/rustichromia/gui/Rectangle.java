package rustichromia.gui;

public class Rectangle {
    int x, y;
    int width, height;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(int x, int y) {
        return x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
    }
}
