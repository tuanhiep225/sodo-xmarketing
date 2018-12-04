package com.sodo.xmarketing.model;

import javax.jdo.annotations.Unique;
import com.sodo.xmarketing.model.entity.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "categories")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Categories extends BaseEntity<String> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Unique
    private String code;

    // Tên danh mục
    @Unique
    private String name;

    // Link cây
    private String path;

    // Tên cha
    private String parent;

    // Tên tiếng việt
    private String vi;

    // Tên tiếng thái
    private String th;

    // Tên tiếng trung
    private String cn;

    // Tên tiếng anh
    private String en;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }


}
