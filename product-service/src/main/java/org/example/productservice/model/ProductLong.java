package org.example.productservice.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "product_long")
public class ProductLong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_short_id", referencedColumnName = "id")
    private ProductShort productShort;

    @Column(name = "length_m")
    private float lengthInM;

    @Column(name = "width_m")
    private float widthInM;

    @Column(name = "height_m")
    private float heightInM;

    @Column(name = "net_weight_kg")
    private float netWeightInKg;

    @Column(name = "gross_weight_kg")
    private float grossWeightInKg;

    public ProductLong() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductShort getProductShort() {
        return productShort;
    }

    public void setProductShort(ProductShort productShort) {
        this.productShort = productShort;
    }

    public float getLengthInM() {
        return lengthInM;
    }

    public void setLengthInM(float lengthInM) {
        this.lengthInM = lengthInM;
    }

    public float getWidthInM() {
        return widthInM;
    }

    public void setWidthInM(float widthInM) {
        this.widthInM = widthInM;
    }

    public float getHeightInM() {
        return heightInM;
    }

    public void setHeightInM(float heightInM) {
        this.heightInM = heightInM;
    }

    public float getNetWeightInKg() {
        return netWeightInKg;
    }

    public void setNetWeightInKg(float netWeightInKg) {
        this.netWeightInKg = netWeightInKg;
    }

    public float getGrossWeightInKg() {
        return grossWeightInKg;
    }

    public void setGrossWeightInKg(float grossWeightInKg) {
        this.grossWeightInKg = grossWeightInKg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductLong that = (ProductLong) o;
        return id == that.id && Float.compare(lengthInM, that.lengthInM) == 0 && Float.compare(widthInM, that.widthInM) == 0 && Float.compare(heightInM, that.heightInM) == 0 && Float.compare(netWeightInKg, that.netWeightInKg) == 0 && Float.compare(grossWeightInKg, that.grossWeightInKg) == 0 && Objects.equals(productShort, that.productShort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productShort, lengthInM, widthInM, heightInM, netWeightInKg, grossWeightInKg);
    }

    @Override
    public String toString() {
        return "ProductLong{" +
                "id=" + id +
                ", lengthInM=" + lengthInM +
                ", widthInM=" + widthInM +
                ", heightInM=" + heightInM +
                ", netWeightInKg=" + netWeightInKg +
                ", grossWeightInKg=" + grossWeightInKg +
                '}';
    }
}
