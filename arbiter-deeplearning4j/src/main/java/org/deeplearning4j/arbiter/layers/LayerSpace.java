/*-
 *
 *  * Copyright 2016 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.deeplearning4j.arbiter.layers;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.FixedValue;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.nd4j.shade.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseLayerSpace contains the common
 * Layer hyperparameters; should match
 * {@link Layer} in terms of features
 *
 * @author Alex Black
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

@Data @NoArgsConstructor(access = AccessLevel.PROTECTED) //For Jackson JSON/YAML deserialization
public abstract class LayerSpace<L extends Layer> implements ParameterSpace<L> {
    protected ParameterSpace<Double> dropOut;
    protected int numParameters;

    @SuppressWarnings("unchecked")
    protected LayerSpace(Builder builder) {
        this.dropOut = builder.dropOut;
    }

    @Override
    public List<ParameterSpace> collectLeaves() {
        List<ParameterSpace> list = new ArrayList<>();
        if (dropOut != null) list.addAll(dropOut.collectLeaves());
        return list;
    }

    @Override
    public int numParameters() {
        return numParameters;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public void setIndices(int... indices) {
        throw new UnsupportedOperationException("Cannot set indices for non-leaf parameter space");
    }


    protected void setLayerOptionsBuilder(Layer.Builder builder, double[] values) {
        if (dropOut != null) builder.dropOut(dropOut.getValue(values));
    }


    @Override
    public String toString() {
        return toString(", ");
    }

    protected String toString(String delim) {
        StringBuilder sb = new StringBuilder();
        if (dropOut != null) sb.append("dropOut: ").append(dropOut).append(delim);
        String s = sb.toString();

        if (s.endsWith(delim)) {
            //Remove final delimiter
            int last = s.lastIndexOf(delim);
            return s.substring(0, last);
        } else return s;
    }

    @SuppressWarnings("unchecked")
    public abstract static class Builder<T> {
        protected ParameterSpace<Double> dropOut;

        public T dropOut(double dropOut) {
            return dropOut(new FixedValue<Double>(dropOut));
        }

        public T dropOut(ParameterSpace<Double> dropOut) {
            this.dropOut = dropOut;
            return (T) this;
        }

        public abstract <E extends LayerSpace> E build();
    }

}
