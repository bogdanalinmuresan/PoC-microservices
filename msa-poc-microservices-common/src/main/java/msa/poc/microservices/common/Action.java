package msa.poc.microservices.common;

import java.io.Serializable;

public enum Action implements Serializable {
        ADD, MODIFY, DELETE;

        public String toString() {
            return this.name();
        }
    }