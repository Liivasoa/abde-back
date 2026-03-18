package mg.msys.abde_back.batch.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GutenbergBook {
    private Long id;
    private String issued;
    private String title;
    private String languages;
    private String authors;
    private String subjects;
}
