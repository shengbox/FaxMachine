/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qingzhi.apps.fax.io;

import java.io.IOException;

/**
 * General {@link java.io.IOException} that indicates a problem occurred while parsing or applying a {@link
 * JSONHandler}.
 */
public class HandlerException extends IOException {

    public HandlerException() {
        super();
    }

    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    @Override
    public String toString() {
        if (getCause() != null) {
            return getLocalizedMessage() + ": " + getCause();
        } else {
            return getLocalizedMessage();
        }
    }

    public static class UnauthorizedException extends HandlerException {
        public UnauthorizedException() {
        }

        public UnauthorizedException(String message) {
            super(message);
        }

        public UnauthorizedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class NoDevsiteProfileException extends HandlerException {
        public NoDevsiteProfileException() {
        }

        public NoDevsiteProfileException(String message) {
            super(message);
        }

        public NoDevsiteProfileException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
