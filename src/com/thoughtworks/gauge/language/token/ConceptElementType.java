/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge.language.token;

import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.Concept;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ConceptElementType extends IElementType {
    public ConceptElementType(@NotNull @NonNls String debugName) {
        super(debugName, Concept.INSTANCE);
    }
}
