import {ChangeEvent, FormEvent, useRef, useState} from "react";
import axios from "axios";
import {useTranslation} from "react-i18next";
import Item from "../../../../../type/Item.tsx";
import {emptyItem} from "../../../../../type/EmptyItem.tsx";
import ItemIdContainer from "../../../../../type/ItemIdContainer.tsx";
import RegistryConfig from "../../../../../type/RegistryConfig.tsx";

export default function NewItemForm(
    {
        config,
        addItemId
    }: {
        readonly config: RegistryConfig,
        readonly addItemId: (itemId: ItemIdContainer) => void,
    }
) {
    const {t} = useTranslation();
    const formRef = useRef<HTMLFormElement>(null);
    const [itemData, setItemData] = useState<Item>(emptyItem);
    const handleSubmit = function (event: FormEvent) {
        event.preventDefault();
        axios.post<ItemIdContainer>(config.item.url, itemData)
            .then(response => {
                addItemId({publicId: response.data.publicId, privateId: response.data.privateId});
                formRef.current?.reset();
                setItemData(emptyItem);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }
    const handleProductDataChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
        setItemData((prevState) => ({
            ...prevState,
            product: {
                ...prevState.product,
                [name]: value,
            },
        }));
    };
    const handleItemDataChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
        setItemData((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };

    return <form className="new-item-form" onSubmit={handleSubmit} ref={formRef}>
        <label htmlFor="title">{t("item_name")}</label>
        <input type="text" name="title" value={itemData.product.title} onChange={(e) => handleProductDataChange(e)}/>
        <label htmlFor="description">{t("item_description")}</label>
        <input type="text" name="description" value={itemData.product.description}
               onChange={(e) => handleProductDataChange(e)}/>
        <label htmlFor="link">{t("item_link")}</label>
        <input type="text" name="link" value={itemData.product.link} onChange={(e) => handleProductDataChange(e)}/>
        <label htmlFor="quantity">{t("item_quantity")}</label>
        <input type="text" name="quantity" value={itemData.quantity} onChange={(e) => handleItemDataChange(e)}/>
        <button>{t("item_add")}</button>
    </form>
}
