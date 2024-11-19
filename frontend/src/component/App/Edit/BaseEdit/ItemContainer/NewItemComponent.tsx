import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {emptyItem} from "../../../../../type/EmptyItem";
import ItemIdContainer from "../../../../../type/ItemIdContainer";
import RegistryConfig from "../../../../../type/RegistryConfig";
import ItemRestricted from "../../../../../type/ItemRestricted.tsx";
import itemFormSchema from "../../../../../schema/ItemFormSchema.tsx";
import {Button, Col, Form, Row} from "react-bootstrap";
import ItemStatus from "../../../../../type/ItemStatus.tsx";
import {useEffect, useRef, useState} from "react";

export default function NewItemComponent(
    {
        config,
        addItemId
    }: {
        readonly config: RegistryConfig,
        readonly addItemId: (itemId: ItemIdContainer) => void,
    }
) {
    const {t} = useTranslation();
    const [productLinkRequired, setProductLinkRequired] = useState<boolean>(true)
    const {
        register,
        handleSubmit,
        reset,
        trigger,
        watch,
        formState: {errors},
    } = useForm<ItemRestricted>({
        resolver: yupResolver(itemFormSchema),
        defaultValues: emptyItem,
    });
    const onProductLinkChange = () => {
        if (!linkValue && !isMounted.current) return;
        isMounted.current = true;
        trigger("product.link").then((result) => {
            setProductLinkRequired(!result);
        }).catch(() => {
            setProductLinkRequired(true);
        });
    }
    const linkValue = watch("product.link");
    const isMounted = useRef(false);

    const onSubmit: SubmitHandler<ItemRestricted> = (data) => {
        axios.post<ItemIdContainer>(config.item.url, data)
            .then(response => {
                addItemId({
                    publicId: response.data.publicId,
                    privateId: response.data.privateId
                });
                isMounted.current = false;
                setProductLinkRequired(true);
                reset();
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    };

    useEffect(onProductLinkChange, [linkValue, errors.product?.link?.message]);

    return (
        <Form className="new-item-form" onSubmit={handleSubmit(onSubmit)}>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_link")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="text"
                        {...register("product.link")}
                        isInvalid={!!errors.product?.link?.message}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.product?.link?.message && t(errors.product.link.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_name")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="text"
                        {...register("product.title")}
                        isInvalid={!!errors.product?.title?.message}
                        disabled={productLinkRequired}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.product?.title?.message && t(errors.product.title.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_description")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        {...register("product.description")}
                        isInvalid={!!errors.product?.description?.message}
                        disabled={productLinkRequired}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.product?.description?.message && t(errors.product.description.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_quantity")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Control
                        type="number"
                        {...register("quantity")}
                        isInvalid={!!errors.quantity?.message}
                        disabled={productLinkRequired}
                    />
                    <Form.Control.Feedback type="invalid">
                        {errors.quantity?.message && t(errors.quantity.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3 align-items-center">
                <Col sm={4} className="text-end">
                    <Form.Label>{t("item_status")}</Form.Label>
                </Col>
                <Col sm={8}>
                    <Form.Select
                        {...register("status")}
                        isInvalid={!!errors.status?.message}
                        disabled
                    >
                        <option value={ItemStatus.AVAILABLE}>{t("status_available")}</option>
                        <option value={ItemStatus.RESERVED}>{t("status_reserved")}</option>
                        <option value={ItemStatus.PURCHASED}>{t("status_purchased")}</option>
                    </Form.Select>
                    <Form.Control.Feedback type="invalid">
                        {errors.status?.message && t(errors.status.message)}
                    </Form.Control.Feedback>
                </Col>
            </Row>
            <Row className="mb-3">
                <Col sm={{span: 8, offset: 4}}>
                    <Button variant="primary" type="submit"
                            disabled={productLinkRequired}>
                        {t("item_add")}
                    </Button>
                </Col>
            </Row>
        </Form>
    );
}
