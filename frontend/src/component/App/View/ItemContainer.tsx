import ItemComponent from "./ItemContainer/ItemComponent.tsx";
import RegistryConfig from "../../../type/RegistryConfig.tsx";
import ItemIdContainer from "../../../type/ItemIdContainer.tsx";
import {Alert, Card, Col, Row} from "react-bootstrap";
import {useTranslation} from "react-i18next";

export default function ItemContainer(
    {
        itemIdList,
        config
    }: {
        readonly itemIdList: ItemIdContainer[],
        readonly config: RegistryConfig
    }
) {
    const {t} = useTranslation();

    return (
        <>
            <Card className="mb-3">
                <Card.Header className="bg-primary text-white">
                    {t("item_list")}
                </Card.Header>
                <Card.Body>
                    <Row className="align-items-center">
                        {itemIdList.length > 0 ? (
                            itemIdList.map(
                                (itemId) => (
                                    <Col sm={4} key={itemId.publicId}>
                                        <ItemComponent itemIdContainer={itemId} config={config}/>
                                    </Col>
                                )
                            )
                        ) : (
                            <Alert variant="info" className="mb-3">{t("no_products_found")}</Alert>
                        )}
                    </Row>
                </Card.Body>
            </Card>
        </>
    );
}
