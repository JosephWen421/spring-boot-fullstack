import {
  Button,
  Drawer, DrawerBody,
  DrawerCloseButton,
  DrawerContent, DrawerFooter, DrawerHeader,
  DrawerOverlay, Stack, useDisclosure
} from "@chakra-ui/react";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";
import React from "react";

const CloseIcon = () => "x";

const UpdateCustomerDrawer = ({fetchCustomers, initialValues, customerId}) => {
  const {isOpen, onOpen, onClose} = useDisclosure()
  return <Stack>
    <Button
        marginX={5}
        marginY={2}
        bg={'blue.400'}
        color={'white'}
        rounded={'full'}
        _hover={{
          transform: 'translateY(-2px)',
          boxShadow: 'lg'
        }}
        _focus={{
          bg: 'green.500'
        }}
        onClick={onOpen}
    >
      Update
    </Button>

    <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
      <DrawerOverlay/>
      <DrawerContent>
        <DrawerCloseButton/>
        <DrawerHeader>Update customer</DrawerHeader>

        <DrawerBody>
          <UpdateCustomerForm
              fetchCustomers={fetchCustomers}
              initialValues={initialValues}
              customerId={customerId}
          />
        </DrawerBody>

        <DrawerFooter>
          <Button
              leftIcon={<CloseIcon/>}
              colorScheme={"teal"}
              onClick={onClose}
          >
            Close
          </Button>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  </Stack>
}

export default UpdateCustomerDrawer;
