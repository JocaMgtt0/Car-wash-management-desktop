module LavaJato {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.persistence;
	requires java.sql;
	requires org.hibernate.orm.core;
    requires java.xml.bind;
	
	
	opens view;
	opens model;
	opens controller;
    opens model.infra to org.hibernate.orm.core;
}