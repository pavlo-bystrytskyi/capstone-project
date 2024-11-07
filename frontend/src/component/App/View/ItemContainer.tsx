import {useEffect, useState} from "react";
import axios from "axios";
import Item from "../../../type/Item.tsx";
import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import ViewConfig from "../../../type/ViewConfig.tsx";

export default function ItemContainer(
    {
        itemIdList,
        config
    }: {
        itemIdList: string[],
        config: ViewConfig
    }
) {
    const [itemList, setItemList] = useState<Item[]>([])
    const loadItems = async function () {
        if (!itemIdList) return;
        try {
            const items = await Promise.all(
                itemIdList.map(
                    async (itemId: string) => (await axios.get<Item>(config.item.url + itemId)).data
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
                (item) => <ItemComponent key={item.publicId} item={item} config={config}/>
            )
        }
    </>
}
