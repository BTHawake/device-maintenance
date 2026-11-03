package method.dto;

import java.util.List;

public record PageResult<T> (
    List<T> records,
    long total,
    long pages,
    long current,
    long size
){}
