public enum ImageType {
    NOT_CLASSIFIED(0),
    FACES(1);

    private final int number;

    ImageType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
