import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import RegistryConfig from "../../../type/RegistryConfig.tsx";

export default function ItemContainer(
    {
        itemIdList,
        config
    }: {
        itemIdList: string[],
        config: RegistryConfig
    }
) {
    return <>
        <h2>Item Container</h2>
        {
            itemIdList.map(
                (itemId) => <ItemComponent key={itemId} itemId={itemId} config={config}/>
            )
        }
    </>
}
