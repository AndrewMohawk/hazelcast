/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.plan.node;

import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.type.QueryDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to scan a map.
 */
public abstract class AbstractMapScanPlanNode extends ZeroInputPlanNode {
    /** Map name. */
    protected String mapName;

    /** Field names. */
    protected List<String> fieldNames;

    /** Field types. */
    protected List<QueryDataType> fieldTypes;

    /** Projects. */
    protected List<Integer> projects;

    /** Filter. */
    protected Expression<Boolean> filter;

    protected AbstractMapScanPlanNode() {
        // No-op.
    }

    protected AbstractMapScanPlanNode(
        int id,
        String mapName,
        List<String> fieldNames,
        List<QueryDataType> fieldTypes,
        List<Integer> projects,
        Expression<Boolean> filter
    ) {
        super(id);

        this.mapName = mapName;
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        this.projects = projects;
        this.filter = filter;
    }

    public String getMapName() {
        return mapName;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public List<QueryDataType> getFieldTypes() {
        return fieldTypes;
    }

    public List<Integer> getProjects() {
        return projects;
    }

    public Expression<Boolean> getFilter() {
        return filter;
    }

    @Override
    public PlanNodeSchema getSchema0() {
        List<QueryDataType> types = new ArrayList<>(projects.size());

        for (Integer project : projects) {
            types.add(fieldTypes.get(project));
        }

        return new PlanNodeSchema(types);
    }

    @Override
    protected void writeData0(ObjectDataOutput out) throws IOException {
        out.writeUTF(mapName);
        SerializationUtil.writeList(fieldNames, out);
        SerializationUtil.writeList(fieldTypes, out);
        SerializationUtil.writeList(projects, out);
        out.writeObject(filter);
    }

    @Override
    protected void readData0(ObjectDataInput in) throws IOException {
        mapName = in.readUTF();
        fieldNames = SerializationUtil.readList(in);
        fieldTypes = SerializationUtil.readList(in);
        projects = SerializationUtil.readList(in);
        filter = in.readObject();
    }
}