package org.xtx.ut4converter.config;

import java.util.Objects;

public class GameConversionConfig {

    private String gameId;

    private Double scale;

    /**
     * Constructor for jackson json lib
     * Do not delete
     */
    public GameConversionConfig() {
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameConversionConfig that = (GameConversionConfig) o;
        return Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }

    @Override
    public String toString() {
        return "GameConversionConfig{" +
                "gameId='" + gameId + '\'' +
                ", scale=" + scale +
                '}';
    }
}
