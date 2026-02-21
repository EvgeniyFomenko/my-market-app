package ru.practicum.mymarketapp.entity;

import jakarta.persistence.*;

@Entity
@Table
public class Item {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "generated_counter")
    @SequenceGenerator(allocationSize = 1, name = "generated_counter")
    private Long id;
    private String title;
    private String description;
    @Column(name = "img_path")
    private String imgPath;
    private long price;
    @Transient
    private int count;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", price=" + price +
                ", count=" + count +
                '}';
    }
}
