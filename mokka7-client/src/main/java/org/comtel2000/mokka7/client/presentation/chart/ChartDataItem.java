package org.comtel2000.mokka7.client.presentation.chart;

import org.comtel2000.mokka7.block.S7DataItem;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.S7;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;

public class ChartDataItem extends S7DataItem {

    private final IntegerProperty maxHistory = new SimpleIntegerProperty(50);
    private final LongProperty value = new SimpleLongProperty();

    private final ObservableList<Data<Number, Number>> list = FXCollections.observableArrayList();
    private final String name;

    public ChartDataItem(AreaType area, DataType type, int db, int start) {
        super(area, type, db, start, 1);
        name = createTitle(area, type, db, start);
    }

    public ObservableList<Data<Number, Number>> getData() {
        return list;
    }

    public LongProperty valueProperty(){
        return value;
    }

    public void create(long time) {

        if (list.size() >= maxHistory.get()) {
            list.remove(0, list.size() - maxHistory.get());
        }
        value.set(readValue());
        list.add(new Data<Number, Number>(time, value.get()));
    }

    private long readValue() {
        switch (type) {
            case WORD:
                return S7.getWordAt(data, 0);
            case DWORD:
                return S7.getDWordAt(data, 0);
            default:
                return S7.getByteAt(data, 0) & 0xFF;
        }
    }

    private static String createTitle(AreaType area, DataType type, int db, int start) {
        switch (area) {
            case DB:
                return String.format("%s%d.%s%d", area, db, toString(type), start);
            case MK:
                return String.format("M%d", start);
            default:
                return String.format("%s%d.%s%d", area, db, toString(type), start);
        }
    }

    private static String toString(DataType type) {
        switch (type) {
            case BIT:
                return "DBX";
            case BYTE:
                return "DBB";
            case WORD:
                return "DBW";
            case DWORD:
                return "DBD";
            default:
                return String.format(" (%s) ", type);
        }
    }

    public IntegerProperty maxHistory() {
        return maxHistory;
    }

    public int getMaxHistory() {
        return maxHistory.get();
    }

    public void setMaxHistory(int maxDataSize) {
        this.maxHistory.set(maxDataSize);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChartDataItem other = (ChartDataItem) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
