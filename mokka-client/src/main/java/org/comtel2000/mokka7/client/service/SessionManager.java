/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2017 J.Zimmermann All rights reserved.
 *
 * Mokka7 is free software: you can redistribute it and/or modify it under the terms of the Lesser
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or under EPL Eclipse Public License 1.0.
 *
 * This means that you have to chose in advance which take before you import the library into your
 * project.
 *
 * SNAP7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 */
package org.comtel2000.mokka7.client.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.slf4j.LoggerFactory;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

public class SessionManager {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private String name;

    private final Properties props = new Properties();

    private Path propPath;

    public SessionManager() {
        setSession(SessionManager.class.getName());
    }

    public void setSession(String name) {
        this.name = name;
        propPath = FileSystems.getDefault().getPath(System.getProperty("user.home"), "." + name + ".properties");
    }

    public void loadSession() {

        if (Files.exists(propPath, LinkOption.NOFOLLOW_LINKS)) {
            try (InputStream is = Files.newInputStream(propPath, StandardOpenOption.READ)) {
                props.load(is);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public Properties getProperties() {
        return props;
    }

    public final String getSessionName() {
        return name;
    }



    @PreDestroy
    public void saveSession() {
        logger.debug("save session: {}", name);
        try (OutputStream outStream = Files.newOutputStream(propPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            props.store(outStream, name + " session properties");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void bind(final BooleanProperty property, final String propertyName) {
        String value = props.getProperty(propertyName);
        if (value != null) {
            property.set(Boolean.valueOf(value));
        }
        property.addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                props.setProperty(propertyName, property.getValue().toString());
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void bind(final ObjectProperty<?> property, final String propertyName, Class<?> type) {
        String value = props.getProperty(propertyName);
        if (value != null) {
            if (type.getName().equals(Color.class.getName())) {
                ((ObjectProperty<Color>) property).set(Color.valueOf(value));
            } else if (type.getName().equals(String.class.getName())) {
                ((ObjectProperty<String>) property).set(value);
            } else {
                ((ObjectProperty<Object>) property).set(value);
            }
        }
        property.addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                props.setProperty(propertyName, property.getValue().toString());
            }
        });
    }

    public void bind(final DoubleProperty property, final String propertyName) {
        String value = props.getProperty(propertyName);
        if (value != null) {
            property.set(Double.valueOf(value));
        }
        property.addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                props.setProperty(propertyName, property.getValue().toString());
            }
        });
    }

    public void bind(final ToggleGroup toggleGroup, final String propertyName) {
        try {
            String value = props.getProperty(propertyName);
            if (value != null) {
                int selectedToggleIndex = Integer.parseInt(value);
                toggleGroup.selectToggle(toggleGroup.getToggles().get(selectedToggleIndex));
            }
        } catch (Exception ignored) {
        }
        toggleGroup.selectedToggleProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                if (toggleGroup.getSelectedToggle() == null) {
                    props.remove(propertyName);
                } else {
                    props.setProperty(propertyName, Integer.toString(toggleGroup.getToggles().indexOf(toggleGroup.getSelectedToggle())));
                }
            }
        });
    }

    public void bind(final ComboBox<?> combo, final String propertyName) {
        try {
            String value = props.getProperty(propertyName);
            if (value != null) {
                combo.getSelectionModel().select(Integer.parseInt(value));
            }
        } catch (Exception ignored) {
        }
        combo.getSelectionModel().selectedIndexProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable o) {
                System.err.println(o.toString());
                int value = ((ReadOnlyIntegerProperty) o).get();
                props.setProperty(propertyName, Integer.toString(value));
            }
        });
    }

    public void bind(final Accordion accordion, final String propertyName) {
        Object selectedPane = props.getProperty(propertyName);
        for (TitledPane tp : accordion.getPanes()) {
            if (tp.getText() != null && tp.getText().equals(selectedPane)) {
                accordion.setExpandedPane(tp);
                break;
            }
        }
        accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

            @Override
            public void changed(ObservableValue<? extends TitledPane> ov, TitledPane t, TitledPane expandedPane) {
                if (expandedPane != null) {
                    props.setProperty(propertyName, expandedPane.getText());
                }
            }
        });
    }

    public void bind(final StringProperty property, final String propertyName) {
        String value = props.getProperty(propertyName);
        if (value != null) {
            property.set(value);
        }

        property.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                props.setProperty(propertyName, property.getValue());
            }
        });
    }



}
