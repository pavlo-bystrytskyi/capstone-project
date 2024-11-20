import NewItemComponent from "./ItemContainer/NewItemComponent.tsx";
import EditItemComponent from "./ItemContainer/EditItemComponent.tsx";
import {useTranslation} from "react-i18next";
import ItemIdContainer from "../../../../type/ItemIdContainer.tsx";
import RegistryConfig from "../../../../type/RegistryConfig.tsx";
import {Alert, Card, Col, Row} from "react-bootstrap";

export default function ItemContainer(
    {
        config,
        itemIdList,
        setItemIdList
    }: {
        readonly config: RegistryConfig,
        readonly itemIdList: ItemIdContainer[],
        readonly setItemIdList: (itemIdList: ItemIdContainer[]) => void
    }
) {
    const {t} = useTranslation();
    const addItemId = function (itemId: ItemIdContainer) {
        setItemIdList([...itemIdList, itemId]);
    }
    const removeItemId = function (itemIdToRemove: ItemIdContainer) {
        setItemIdList(
            itemIdList.filter(
                (itemId) => itemIdToRemove !== itemId)
        );
    }

    return (
        <>
            <Card className="mb-3">
                <Card.Header className="bg-primary text-white">
                    {t("item_list")}
                </Card.Header>
                <Card.Body>
                    <Row className="align-items-center">
                        <Col sm={4}>
                            <NewItemComponent config={config} addItemId={addItemId}/>
                        </Col>
                        {itemIdList.length > 0 ? (
                            itemIdList.map(
                                (itemId) => (
                            <Col sm={4}>
                                <EditItemComponent
                                    config={config}
                                    itemId={itemId}
                                    removeItemId={removeItemId}
                                />
                            </Col>
                                )
                            )
                        ) : (
                            <Alert variant="info" className="mb-3">{t("add_some_products")}</Alert>
                        )}
                    </Row>
                </Card.Body>
            </Card>
        </>
    );
}
