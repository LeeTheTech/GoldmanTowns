package lee.code.towns.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GlobalValue {
  CLAIM_TAX_AMOUNT(10),
  RENT_MAX_PRICE(1000000)
  ;
  @Getter private final double value;
}
