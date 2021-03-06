/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.graalvm.component.installer.persist.test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Handler extends URLStreamHandler {
    private static Map<String, URL> bindings = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, URLConnection> connections = Collections.synchronizedMap(new HashMap<>());
    private static Set<URL> visitedURLs = Collections.synchronizedSet(new HashSet<>());

    public Handler() {
    }

    public static void bind(String s, URL u) {
        bindings.put(s, u);
    }

    public static void bind(String s, URLConnection con) {
        connections.put(s, con);
    }

    public static void clear() {
        bindings.clear();
        connections.clear();
        visitedURLs.clear();
    }

    public static void clearVisited() {
        visitedURLs.clear();
    }

    public static boolean isVisited(URL u) {
        return visitedURLs.contains(u);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        URLConnection c = connections.get(u.toString());
        visitedURLs.add(u);
        if (c != null) {
            return c;
        }
        URL x = bindings.get(u.toString());
        if (x != null) {
            return x.openConnection();
        }
        throw new IOException("Unsupported");
    }
}
