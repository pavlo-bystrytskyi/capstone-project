import {useEffect, useState} from "react";
import axios from "axios";
import Item from "../../../type/Item.tsx";
import ItemComponent from "./ItemContainer/ItemComponent.tsx";

export default function ItemContainer(
    {
        itemIdList
    }: {
        itemIdList: string[],
    }
) {
    const [itemList, setItemList] = useState<Item[]>([])
    const loadItems = async function () {
        if (!itemIdList) return;
        try {
            const items = await Promise.all(
                itemIdList.map(
                    async (itemId: string) => (await axios.get<Item>("/api/item/public/" + itemId)).data
                )
            );
            setItemList(items);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
    useEffect(
        () => {
            loadItems();
        },
        [itemIdList]
    );

    return <>
        <h2>Item Container</h2>
        {
            itemList.map(
                (item) => <ItemComponent key={item.publicId} item={item}/>
            )
        }
    </>
}
