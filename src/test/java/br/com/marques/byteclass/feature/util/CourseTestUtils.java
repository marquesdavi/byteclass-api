package br.com.marques.byteclass.feature.util;

import br.com.marques.byteclass.feature.course.port.dto.CourseRequest;

public class CourseTestUtils {
    public static class CourseRequestBuilder {
        private String title = "Java 101";
        private String description = "Intro to Java";

        public CourseRequestBuilder title(String t) { this.title = t; return this; }
        public CourseRequestBuilder description(String d) { this.description = d; return this; }
        public CourseRequest build() { return new CourseRequest(title, description); }
    }
}
