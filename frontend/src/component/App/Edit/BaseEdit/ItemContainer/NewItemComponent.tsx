import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {emptyItem} from "../../../../../type/EmptyItem";
import ItemIdContainer from "../../../../../type/ItemIdContainer";
import RegistryConfig from "../../../../../type/RegistryConfig";
import ItemRestricted from "../../../../../type/ItemRestricted.tsx";
import itemFormSchema from "../../../../../schema/ItemFormSchema.tsx";
import {Button, Col, Form, Row, Spinner} from "react-bootstrap";
import ItemStatus from "../../../../../type/ItemStatus.tsx";
import {useEffect, useRef, useState} from "react";
import useToast from "../../../../../context/toast/UseToast.tsx";
import ToastVariant from "../../../../../context/toast/ToastVariant.tsx";
import Product from "../../../../../type/Product.tsx";

export default function NewItemComponent(
    {
        config,
        addItemId,
        isAiProcessingEnabled
    }: {
        readonly config: RegistryConfig,
        readonly addItemId: (itemId: ItemIdContainer) => void,
        readonly isAiProcessingEnabled: boolean
    }
) {
    const {t} = useTranslation();
    const [productLinkRequired, setProductLinkRequired] = useState<boolean>(true)
    const {
        register,
        handleSubmit,
        reset,
        setValue,
        trigger,
        watch,
        formState: {errors},
    } = useForm<ItemRestricted>({
        resolver: yupResolver(itemFormSchema),
        defaultValues: emptyItem,
    });
    const {addToast} = useToast();
    const [showLoader, setShowLoader] = useState(false);
    const onProductLinkChange = () => {
        if (!linkValue && !isMounted.current) return;
        isMounted.current = true;
        trigger("product.link").then((result) => {
            setProductLinkRequired(!result);
            if (result && isAiProcessingEnabled) {
                setShowLoader(true);
                const request = {
                    url: linkValue
                }
                axios.post<Product>(
                    "/api/extraction", request
                ).then(
                    result => {
                        console.log(result.data);
                        setValue("product", {...result.data, link: linkValue});
                        addToast(t("toast_ai_processing_success"), ToastVariant.SUCCESS);
                        setShowLoader(false);
                    }
                ).catch((error) => {
                    console.error('Error fetching data:', error);
                    addToast(t("toast_ai_processing_failed"), ToastVariant.INFO);
                    setShowLoader(false);
                });
            }
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
                addToast(t("toast_item_save_successful"), ToastVariant.SUCCESS);
            })
            .catch(
                error => {
                    console.error("Error fetching data:", error);
                    addToast(t("toast_item_save_failed"), ToastVariant.ERROR);
                });
    };

    useEffect(onProductLinkChange, [linkValue, errors.product?.link?.message]);

    return (
        <Form className="new-item-form" onSubmit={handleSubmit(onSubmit)} style={{position: 'relative'}}>
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
                        rows={10}
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
            {showLoader && (
                <div
                    className="position-absolute top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center bg-light bg-opacity-50">
                    <Spinner animation="border"/>
                </div>
            )}
        </Form>
    );
}
