import {ChangeEvent, FormEvent, useEffect, useState} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import Item from "../../../../../type/Item.tsx";
import {emptyItem} from "../../../../../type/EmptyItem.tsx";
import ItemIdContainer from "../../../../../type/ItemIdContainer.tsx";
import ItemStatus from "../../../../../type/ItemStatus.tsx";

export default function ItemComponent(
    {
        itemId,
        removeItemId
    }: {
        itemId: ItemIdContainer,
        readonly removeItemId: (itemId: ItemIdContainer) => void
    }
) {
    const {t} = useTranslation();
    const [item, setItem] = useState<Item>(emptyItem)
    const removeItem = function (event: FormEvent) {
        event.preventDefault();
        axios.delete('/api/item/' + itemId.privateId)
            .then(() => {
                removeItemId(itemId);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
    const saveItem = function (event: FormEvent) {
        event.preventDefault();
        axios.put('/api/item/' + itemId.privateId, item)
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
    const loadItem = function () {
        axios.get<Item>("/api/item/" + itemId.privateId)
            .then(result => {
                    setItem(result.data);
                }
            ).catch(error => {
            console.error('Error fetching data:', error);
        });
    }
    useEffect(loadItem, [itemId]);

    const handleProductDataChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
        setItem((prevState) => ({
            ...prevState,
            product: {
                ...prevState.product,
                [name]: value,
            },
        }));
    };
    const handleItemDataChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
        setItem((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };
    const handleItemStatusChange = (event: ChangeEvent<HTMLSelectElement>) => {
        const {value} = event.target;
        setItem((prevState) => ({
            ...prevState,
            status: value as ItemStatus,
        }));
    };

    return <form className="item-form">
        <label htmlFor="title">{t("item_name")}</label>
        <input type="text" name="title" value={item.product.title} onChange={handleProductDataChange}/>
        <label htmlFor="description">{t("item_description")}</label>
        <input type="text" name="description" value={item.product.description} onChange={handleProductDataChange}/>
        <label htmlFor="link">{t("item_link")}</label>
        <input type="text" name="link" value={item.product.link} onChange={handleProductDataChange}/>
        <label htmlFor="quantity">{t("item_quantity")}</label>
        <input type="text" name="quantity" value={item.quantity} onChange={handleItemDataChange}/>
        <select name="status" onChange={handleItemStatusChange}
                value={item.status}>
            <option value={ItemStatus.AVAILABLE}>{t("status_available")}</option>
            <option value={ItemStatus.RESERVED}>{t("status_reserved")}</option>
            <option value={ItemStatus.PURCHASED}>{t("status_purchased")}</option>
        </select>
        <button onClick={saveItem}>{t("item_save")}</button>
        <button onClick={removeItem}>{t("item_remove")}</button>
    </form>
}
