/**
 * 
 */
package brix.jcr.model.adapter;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;

abstract class PropertyAdapter implements IModel
{
    abstract String getName();

    abstract void save();

    abstract JcrNode getNode();
}