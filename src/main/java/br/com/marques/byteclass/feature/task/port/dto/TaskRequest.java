package br.com.marques.byteclass.feature.task.port.dto;

public interface TaskRequest {
    Long getCourseId();
    String getStatement();
    Integer getOrder();
}
