package model;

public class Note {
    public int id;
    public String title;
    public String content;

    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return title;
    }
}