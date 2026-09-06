package ua.world.redis;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter

public class Language {
    private String language;

    public Boolean getOfficial() {
        return isOfficial;
    }

    public void setOfficial(Boolean official) {
        isOfficial = official;
    }

    private Boolean isOfficial;
    private BigDecimal percentage;
}
 