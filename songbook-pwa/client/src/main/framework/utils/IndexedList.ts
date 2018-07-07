import * as _ from "lodash";

import {toMap} from "main/framework/utils/Utils";

export interface IndexedList<ID,T> {
    getItems() : T[];
    getId(item : T) : ID;
    getItem(id : ID) : T;
    hasItem(id : ID) : boolean;

    withItems(items: T[]) : IndexedList<ID,T>;
    withoutItem(oldId : ID) : IndexedList<ID,T>;
    withUpdatedItemById(oldId: ID, newItem: T) : IndexedList<ID,T>;
    withUpdatedItem(oldItem: T, newItem: T) : IndexedList<ID,T>;
}


export class PersistentIndexedList<ID,T> implements IndexedList<ID,T> {
    private items : T[];
    private idMapper : (item : T) => ID;
    private itemToIdMap: Map<ID,T>;
    private sortKey: (item : T) => string | number;

    static createDirect<T>(items: T[]) : IndexedList<T,T> {
        return new PersistentIndexedList(items, item => item, null);
    }

    static createDirectSorted<T>(items: T[], sortKey: (item : T) => string | number) : IndexedList<T,T> {
        return new PersistentIndexedList(items, item => item, sortKey);
    }

    static create<ID, T>(items: T[], idMapper : (item : T) => ID) : IndexedList<ID,T> {
        return new PersistentIndexedList(items, idMapper, null);
    }

    static createSorted<ID,T>(items: T[], idMapper : (item : T) => ID, sortKey: (item : T) => string | number) : IndexedList<ID,T> {
        return new PersistentIndexedList(items, idMapper, sortKey);
    }

    private constructor(items: T[], idMapper : (item : T) => ID, sortKey: (item : T) => string | number) {
        this.items = _.isNil(sortKey) ? items : _.sortBy(items, this.sortKey);
        this.idMapper = idMapper;
        this.itemToIdMap = toMap(items, idMapper, it => it);
        this.sortKey = sortKey;
    }

    public getItems() : T[] {
        return this.items;
    }

    public getId(item : T) : ID {
        return this.idMapper(item);
    }

    public getItem(id : ID) : T {
        const item = this.itemToIdMap.get(id);
        return _.isNil(item) ? null : item;
    }

    public hasItem(id : ID) : boolean {
        return this.getItem(id) !== null;
    }

    public withItems(items: T[]) : IndexedList<ID,T> {
        return new PersistentIndexedList<ID,T>(items, this.idMapper, this.sortKey);
    }

    public withoutItem(oldId : ID) : IndexedList<ID,T> {
        if (_.isNil(oldId) || !this.hasItem(oldId)) {
            return this;
        } else {
            const itemsWithoutOldId = this.items.filter(item => this.idMapper(item) !== oldId);
            return this.withItems(itemsWithoutOldId);
        }
    }

    public withUpdatedItemById(oldId: ID, newItem: T) : IndexedList<ID,T> {
        if (_.isNil(newItem)) {
            return  this.withoutItem(oldId);
        }

        const newItemId = this.idMapper(newItem);

        // Remove old items
        let updatedItems = this.items.filter(item => this.idMapper(item) !== oldId && this.idMapper(item) !== newItemId);
        updatedItems.push(newItem);
        return this.withItems(updatedItems);
    }

    public withUpdatedItem(oldItem: T, newItem: T) : IndexedList<ID,T> {
        return (_.isNil(oldItem))
             ? this.withUpdatedItemById(null, newItem)
             : this.withUpdatedItemById(this.idMapper(oldItem), newItem);
    }
}