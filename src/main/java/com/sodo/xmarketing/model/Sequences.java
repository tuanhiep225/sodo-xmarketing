package com.sodo.xmarketing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Henry Do User: henrydo Date: 13/08/2018 Time: 18/16
 */
@Document(collection = "sequences")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sequences {

  @Id
  private String id;

  private int seq;
}
